package com.example.localloop.model;

import android.util.Log;

import com.example.localloop.resources.datetime.Time;
import com.example.localloop.resources.datetime.Date;
import com.example.localloop.resources.exception.InvalidEventCategoryNameException;
import com.example.localloop.resources.exception.InvalidEventNameException;
import com.example.localloop.resources.exception.InvalidJoinRequestException;
import com.example.localloop.resources.exception.NoSuchEventCategoryException;
import com.example.localloop.resources.exception.NoSuchEventException;
import com.example.localloop.resources.exception.NoSuchReservationException;
import com.example.localloop.resources.exception.NoSuchUserException;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class DatabaseConnection {

    private static final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    private static UserAccount user;

    private static ArrayList<Participant> allParticipants = new ArrayList<>();
    private static ArrayList<Organizer> allOrganizers = new ArrayList<>();
    private static ArrayList<Admin> allAdmins = new ArrayList<>();
    private static ArrayList<EventCategory> allEventCategories = new ArrayList<>();
    private static ArrayList<Event> allEvents = new ArrayList<>();
    private static ArrayList<JoinRequest> allJoinRequests = new ArrayList<>();
    private static ArrayList<Reservation> allReservations = new ArrayList<>();
    private static boolean participantsLoaded = false;
    private static boolean organizersLoaded = false;
    private static boolean adminsLoaded = false;
    private static boolean eventCategoriesLoaded = false;
    private static boolean eventsLoaded = false;
    private static boolean joinRequestsLoaded = false;
    private static boolean reservationsLoaded = false;

    // Public methods
    public DatabaseConnection(String username, String password) throws NoSuchUserException, InterruptedException {
        updateAllUsers();

        boolean found = false;

        for (Participant p : allParticipants) {
            if (p.getUsername().equals(username) && p.getPassword().equals(password)) {
                user = p;
                found = true;
                break;
            }
        }
        if (!found) {
            for (Organizer o : allOrganizers) {
                if (o.getUsername().equals(username) && o.getPassword().equals(password)) {
                    user = o;
                    found = true;
                    break;
                }
            }
        }
        if (!found) {
            for (Admin a : allAdmins) {
                if (a.getUsername().equals(username) && a.getPassword().equals(password)) {
                    user = a;
                    found = true;
                    break;
                }
            }
        }

        if (!found) {
            throw new NoSuchUserException("User does not exist");
        }
    }

    public UserAccount getUser() {
        return user;
    }

    public Event getEventFromID(String eventID) throws NoSuchEventException, InterruptedException {
        updateAllEvents();

        for (Event e : allEvents) {
            if (e.getEventID().equals(eventID)) {
                return e;
            }
        }
        throw new NoSuchEventException("Event does not exist");
    }

    public static void createNewUser(Participant p) throws InvalidEventNameException, InterruptedException {
        updateAllUsers();
        for (Participant existing : allParticipants) {
            if (p.getUsername().equals(existing.getUsername())) {
                Log.d(p.getUsername(), "New Participant Username Conflict");
                throw new InvalidEventNameException("Username taken");
            }
        }
        String key = myRef.push().getKey();
        myRef.child("users/Participant").child(key).setValue(new Participant(p.getUsername(), p.getPassword(), key));
    }

    public static void createNewUser(Organizer o) throws InvalidEventNameException, InterruptedException {
        updateAllUsers();
        for (Organizer existing : allOrganizers) {
            if (o.getUsername().equals(existing.getUsername())) {
                Log.e(o.getUsername(), "New Organizer Username Conflict");
                throw new InvalidEventNameException("Username taken");
            }
        }
        String key = myRef.push().getKey();
        myRef.child("users/Organizer").child(key).setValue(new Organizer(o.getUsername(), o.getPassword(), key));
    }

    // Protected methods
    protected static void createEventCategory(EventCategory category) throws InvalidEventCategoryNameException, InterruptedException {
        // Called by Admin Class Only
        updateAllEventCategories();

        // Check that Event Category does not exist
        for (EventCategory existing : allEventCategories) {
            if (category.getName().equals(existing.getName())) {
                Log.e(category.getName(), "New EventCategory Name Conflict");
                throw new InvalidEventCategoryNameException("EventCategory Name taken");
            }
        }
        String key = myRef.push().getKey();
        myRef.child("categories").child(key).setValue(new EventCategory(category.getName(),category.getDescription(),key));
    }
    protected static void deleteEventCategory(EventCategory categoryToDelete) throws NoSuchEventCategoryException, NoSuchEventException, InterruptedException {
        // Called by Admin Class Only
        updateAllEventCategories();
        boolean found = false;
        // Check that Event Category exists
        for (EventCategory existing : allEventCategories) {
            if (categoryToDelete.getCategoryID().equals(existing.getCategoryID())) {
                found = true;
            }
        }
        if(!found) {
            Log.e(categoryToDelete.getName(), "EventCategory Not Found");
            throw new NoSuchEventCategoryException("EventCategory does not exist");
        }
        // Delete all associated events (which inherently deletes all associated JoinRequests and Reservations
        updateAllEvents();
        for (Event e : allEvents) {
            if ((e.getCategoryID()).equals(categoryToDelete.getCategoryID())) {
                deleteEvent(e);
            }
        }
        // Delete Event Category
        myRef.child("categories").child(categoryToDelete.getCategoryID()).removeValue();
    }
    protected static void editEventCategory(EventCategory categoryToEdit, String name, String description) throws NoSuchEventCategoryException, InvalidEventNameException, InterruptedException {
        // Called by Admin Class Only
        updateAllEventCategories();
        boolean found = false;
        boolean duplicate = false;
        // Check that Event Category exists
        for (EventCategory existing : allEventCategories) {
            if (categoryToEdit.getCategoryID().equals(existing.getCategoryID())) {
                found = true;
            }
            if (name.equals(existing.getName()) && !((categoryToEdit.getCategoryID()).equals(existing.getCategoryID()))) {
                duplicate = true;
            }
        }
        if(!found) {
            Log.e(categoryToEdit.getName(), "EventCategory Not Found");
            throw new NoSuchEventCategoryException("EventCategory does not exist");
        }
        if(duplicate) {
            Log.e(categoryToEdit.getName(), "EventCategory name conflict");
            throw new InvalidEventNameException("EventCategory Name taken");
        }
        myRef.child("categories").child(categoryToEdit.getCategoryID()).child("name").setValue(name);
        myRef.child("categories").child(categoryToEdit.getCategoryID()).child("description").setValue(description);
    }
    protected static void createEvent(Event event) throws NoSuchEventCategoryException, InvalidEventNameException, InterruptedException {
        // Called by Organizer Class Only
        updateAllEvents();

        // Check that EventCategory exists
        boolean categoryFound = false;
        for (EventCategory existing : allEventCategories) {
            if ((event.getCategoryID().equals(existing.getCategoryID()))) {
                categoryFound = true;
            }
        }
        if (!categoryFound) {
            throw new NoSuchEventCategoryException("Event category does not exist");
        }

        // Check that Event does not exist
        for (Event existing : allEvents) {
            if (event.getName().equals(existing.getName())) {
                Log.e(event.getName(), "New Event Name Conflict");
                throw new InvalidEventNameException("Event name taken");
            }
        }
        String key = myRef.push().getKey();
        myRef.child("events").child(key).setValue(new Event(event.getName(),event.getDescription(),event.getCategoryID(),event.getFee(),event.getDate(),event.getTime(),user.getUserID(),key));
    }
    protected static void editEvent(Event eventToEdit, Event editedEvent) throws NoSuchEventException, InvalidEventNameException, InterruptedException, NoSuchEventCategoryException {
        // Called by Organizer Class Only
        updateAllEvents();

        // Check that EventCategory exists
        boolean categoryFound = false;
        for (EventCategory existing : allEventCategories) {
            if ((editedEvent.getCategoryID().equals(existing.getCategoryID()))) {
                categoryFound = true;
            }
        }
        if (!categoryFound) {
            Log.e(editedEvent.getName(), "EventCategory does not exist");
            throw new NoSuchEventCategoryException("Event category does not exist");
        }
        // Check that Event does not exist
        for (Event existing : allEvents) {
            if (editedEvent.getName().equals(existing.getName()) && !((user.getUserID()).equals(existing.getOrganizerID()))) {
                Log.e(editedEvent.getName(), "New Event Name Conflict");
                throw new InvalidEventNameException("Event name taken");
            }
        }
        myRef.child("events").child(eventToEdit.getEventID()).setValue(editedEvent);

    }
    protected static void deleteEvent(Event eventToDelete) throws NoSuchEventException, InterruptedException {
        // Called by Organizer Class Only
        updateAllEvents();
        boolean found = false;
        // Check that Event exists
        for (Event existing : allEvents) {
            if ((eventToDelete.getEventID()).equals(existing.getEventID())) {
                found = true;
            }
        }
        if(!found) {
            Log.e(eventToDelete.getName(), "Event Not Found");
            throw new NoSuchEventException("Event does not exist");
        }
        myRef.child("events").child(eventToDelete.getEventID()).removeValue();
        // Delete all associated joinRequests
        updateAllJoinRequests();
        for (JoinRequest r : allJoinRequests) {
            if ((r.getEventID()).equals(eventToDelete.getEventID())) {
                myRef.child("joinRequests").child(r.getJoinRequestID()).removeValue();
            }
        }
        // Delete all associated Reservations
        updateAllReservations();
        for (Reservation r : allReservations) {
            if ((r.getEventID()).equals(eventToDelete.getEventID())) {
                myRef.child("reservations").child(r.getReservationID()).removeValue();
            }
        }
    }
    protected static void deleteUser(UserAccount userToDelete) throws NoSuchUserException, NoSuchEventException, InterruptedException {
        // Called by Admin Class Only
        updateAllUsers();
        boolean found = false;

        if (user instanceof Organizer) {
            // Check that user exists
            for (Organizer o : allOrganizers) {
                if (o.getUserID().equals(userToDelete.getUserID())) {
                    found = true;
                }
            }
            if (!found) {
                Log.e("NoSuchUserException", "Nonexisting user cannot be deleted");
                throw new NoSuchUserException("Nonexisting user cannot be deleted");
            }
            myRef.child("users/Organizer").child(userToDelete.getUserID()).removeValue();
            // Delete all associated events (which inherently deletes all associated JoinRequests and Reservations
            updateAllEvents();
            for (Event e : allEvents) {
                if ((e.getOrganizerID()).equals(userToDelete.getUserID())) {
                    deleteEvent(e);
                }
            }
        } else {
            // Check that user exists
            for (Participant p : allParticipants) {
                if (p.getUserID().equals(userToDelete.getUserID())) {
                    found = true;
                }
            }
            if (!found) {
                Log.e("NoSuchUserException", "Nonexisting user cannot be deleted");
                throw new NoSuchUserException("Nonexisting user cannot be deleted");
            }
            myRef.child("users/Participant").child(userToDelete.getUserID()).removeValue();
            // Delete all associated join requests and reservations
            updateAllJoinRequests();
            for (JoinRequest r : allJoinRequests) {
                if ((user.getUserID()).equals(r.getParticipantID())) {
                    myRef.child("joinRequests").child(r.getJoinRequestID()).removeValue();
                }
            }
            updateAllReservations();
            for (Reservation r : allReservations) {
                if ((user.getUserID()).equals(r.getAttendeeID())) {
                    myRef.child("reservations").child(r.getReservationID()).removeValue();
                }
            }
        }
    }
    protected static void deleteUser(String userID) throws NoSuchUserException, NoSuchEventException, InterruptedException {
        // Called by Admin Class Only
        updateAllUsers();
        boolean found = false;

        // Check that user exists
        for (Organizer o : allOrganizers) {
            if (o.getUserID().equals(userID)) {
                found = true;
                myRef.child("users/Organizer").child(userID).removeValue();
                // Delete all associated events (which inherently deletes all associated JoinRequests and Reservations
                updateAllEvents();
                for (Event e : allEvents) {
                    if ((e.getOrganizerID()).equals(userID)) {
                        deleteEvent(e);
                    }
                }
            }
        }
        for (Participant p : allParticipants) {
            if (p.getUserID().equals(userID)) {
                found = true;
                myRef.child("users/Participant").child(userID).removeValue();
                // Delete all associated join requests and reservations
                updateAllJoinRequests();
                for (JoinRequest r : allJoinRequests) {
                    if (userID.equals(r.getParticipantID())) {
                        myRef.child("joinRequests").child(r.getJoinRequestID()).removeValue();
                    }
                }
                updateAllReservations();
                for (Reservation r : allReservations) {
                    if ((userID).equals(r.getAttendeeID())) {
                        myRef.child("reservations").child(r.getReservationID()).removeValue();
                    }
                }
            }
        }
        if (!found) {
            Log.e("NoSuchUserException", "Nonexisting user cannot be deleted");
            throw new NoSuchUserException("Nonexisting user cannot be deleted");
        }
    }
    protected ArrayList<Participant> getAllParticipants() throws InterruptedException {
        // Called by Admin Class Only
        updateAllUsers(); //
        return allParticipants;
    }
    protected ArrayList<Organizer> getAllOrganizers() throws InterruptedException {
        // Called by Admin Class Only
        updateAllUsers(); //
        return allOrganizers;
    }
    protected ArrayList<EventCategory> getAllEventCategories() throws InterruptedException {
        // Called by Admin, Organizer and Participant Class Only
        updateAllEventCategories(); //
        return allEventCategories;
    }
    protected ArrayList<Event> getAllEvents() throws InterruptedException {
        // Called by Admin and Participant Class only
        updateAllEvents(); //
        return allEvents;
    }
    protected ArrayList<Event> getUserEvents() throws InterruptedException {
        // Called by Organizer Class only
        updateAllEvents(); //
        ArrayList<Event> userEvents = new ArrayList<>();
        for (Event e : allEvents) {
            if ((e.getOrganizerID()).equals(user.getUserID())) {
                userEvents.add(e);
            }
        }
        return userEvents;
    }
    protected void requestJoinEvent(Event event) throws InvalidJoinRequestException, NoSuchEventException, InterruptedException {
        // Called by Participant Class only
        updateAllEvents();
        boolean found = false;
        for (Event e : allEvents) {
            if ((e.getEventID()).equals(event.getEventID())) {
                found = true;
                break;
            }
        }
        if (!found) {
            throw new NoSuchEventException("Request to join non-existing event");
        }
        updateAllJoinRequests();
        for (JoinRequest j : allJoinRequests) {
            if ((j.getParticipantID()).equals(user.getUserID())) {
                if ((j.getEventID()).equals(event.getEventID())) {
                    throw new InvalidJoinRequestException("Join request has already been made");
                }
            }
        }
        String key = myRef.push().getKey();
        myRef.child("joinRequests").child(key).setValue(new JoinRequest(user.getUserID(),event.getEventID(),key));
    }
    protected ArrayList<Participant> getJoinRequests(Event event) throws InterruptedException {
        // Called by Organizer Class only
        updateAllJoinRequests();
        updateAllUsers();

        ArrayList<String> userIDs = new ArrayList<>();
        ArrayList<Participant> requests = new ArrayList<>();

        for (JoinRequest r : allJoinRequests) {
            if ((r.getEventID()).equals(event.getEventID())) {
                userIDs.add(r.getParticipantID());
            }
        }
        for (Participant p : allParticipants) {
            for (String ID : userIDs) {
                if (ID.equals(p.getUserID())) {
                    requests.add(p);
                }
            }
        }
        return requests;
    }
    protected ArrayList<Event> getJoinRequests() throws InterruptedException {
        // Called by Participant Class only
        updateAllJoinRequests();
        updateAllEvents();

        ArrayList<String> eventIDs = new ArrayList<>();
        ArrayList<Event> requests = new ArrayList<>();

        for (JoinRequest r : allJoinRequests) {
            if ((user.getUserID()).equals(r.getParticipantID())) {
                eventIDs.add(r.getEventID());
            }
        }

        for (Event e : allEvents) {
            for (String ID : eventIDs) {
                if (ID.equals(e.getEventID())) {
                    requests.add(e);
                }
            }
        }
        return requests;
    }
    protected void acceptJoinRequest(Participant participant, Event event) throws InterruptedException {
        // Called by Organizer Class only
        updateAllJoinRequests();
        updateAllUsers();

        JoinRequest request = null;

        for (JoinRequest r : allJoinRequests) {
            if ((r.getParticipantID()).equals(participant.getUserID())) {
                if ((r.getEventID()).equals(event.getEventID())) {
                    request = r;
                }
            }
        }
        // TODO no such request error handling
        myRef.child("joinRequests").child(request.getJoinRequestID()).removeValue();
        String key = myRef.push().getKey();
        myRef.child("reservations").child(key).setValue(new Reservation(participant.getUserID(),event.getEventID(),key));
    }
    protected void rejectJoinRequest(Participant participant, Event event) throws InterruptedException {
        // Called by Organizer Class only
        updateAllJoinRequests();
        updateAllUsers();

        JoinRequest request = null;

        for (JoinRequest r : allJoinRequests) {
            if ((r.getParticipantID()).equals(participant.getUserID())) {
                if ((r.getEventID()).equals(event.getEventID())) {
                    request = r;
                }
            }
        }
        // TODO no such request error handling
        myRef.child("joinRequests").child(request.getJoinRequestID()).removeValue();
    }
    protected void cancelReservation(Event event) throws NoSuchReservationException, InterruptedException {
        // Called by Participant Class only
        updateAllReservations();
        Reservation reservation = null;
        boolean found = false;
        for (Reservation r : allReservations) {
            if ((r.getEventID()).equals(event.getEventID())) {
                if ((r.getAttendeeID()).equals(user.getUserID())) {
                    found = true;
                    reservation = r;
                    break;
                }
            }
        }
        if (!found) {
            throw new NoSuchReservationException("Reservation does not exist");
        }
        myRef.child("reservations").child(reservation.getReservationID()).removeValue();
    }
    protected ArrayList<Event> getReservations() throws InterruptedException {
        // Called by Participant Class only
        updateAllReservations();
        updateAllEvents();

        ArrayList<String> eventIDs = new ArrayList<>();
        ArrayList<Event> requests = new ArrayList<>();

        for (Reservation r : allReservations) {
            if ((user.getUserID()).equals(r.getAttendeeID())) {
                eventIDs.add(r.getEventID());
            }
        }

        for (Event e : allEvents) {
            for (String ID : eventIDs) {
                if (ID.equals(e.getEventID())) {
                    requests.add(e);
                }
            }
        }
        return requests;
    }
    protected ArrayList<Participant> getReservations(Event event) throws InterruptedException {
        // Called by Organizer Class only
        updateAllReservations();
        updateAllUsers();

        ArrayList<String> userIDs = new ArrayList<>();
        ArrayList<Participant> reservations = new ArrayList<>();

        for (Reservation r : allReservations) {
            if ((event.getEventID()).equals(r.getEventID())) {
                userIDs.add(r.getAttendeeID());
            }
        }

        for (Participant p : allParticipants) {
            for (String ID : userIDs) {
                if (ID.equals(p.getUserID())) {
                    reservations.add(p);
                }
            }
        }
        return reservations;
    }
    protected void removeReservations(Participant participant, Event event) throws InterruptedException {
        // Called by Organizer Class only
        updateAllReservations();
        updateAllUsers();

        Reservation reservation = null;

        for (Reservation r : allReservations) {
            if ((r.getAttendeeID()).equals(participant.getUserID())) {
                if ((r.getEventID()).equals(event.getEventID())) {
                    reservation = r;
                    break;
                }
            }
        }

        myRef.child("reservations").child(reservation.getReservationID()).removeValue();
    }
    protected Event eventSearch(String name) throws NoSuchEventException, InterruptedException {
        // Called by Participant Class only
        updateAllEvents();
        Event event = null;
        boolean found = false;
        for (Event e : allEvents) {
            if (name.equals(e.getName())) {
                event = e;
                found = true;
            }
        }
        if (!found) {
            throw new NoSuchEventException("Event does not exist");
        }
        return event;
    }
    protected ArrayList<Event> eventSearch(EventCategory category) throws NoSuchEventCategoryException, InterruptedException {
        // Called by Participant Class only
        updateAllEventCategories();
        updateAllEvents();
        ArrayList<Event> events = new ArrayList<>();
        boolean found = false;
        for (EventCategory cat : allEventCategories) {
            if ((cat.getCategoryID()).equals(category.getCategoryID())) {
                found = true;
            }
        }
        if (!found) {
            throw new NoSuchEventCategoryException("Event category does not exist");
        }
        for (Event e : allEvents) {
            if ((category.getCategoryID()).equals(e.getCategoryID())) {
                events.add(e);
            }
        }
        return events;
    }
    // Private Methods
    private interface DatabaseUserCallback {
        void onParticipantsLoaded(ArrayList<Participant> participants);
        void onOrganizersLoaded(ArrayList<Organizer> organizers);
        void onAdminsLoaded(ArrayList<Admin> admins);
    }
    private interface DatabaseEventCategoryCallback {
        void onEventCategoriesLoaded(ArrayList<EventCategory> categories);
    }
    private interface DatabaseEventCallback {
        void onEventsLoaded(ArrayList<Event> events);
    }
    private interface DatabaseJoinRequestCallback {
        void onJoinRequestsLoaded(ArrayList<JoinRequest> joinRequests);
    }
    private interface DatabaseReservationCallback {
        void onReservationsLoaded(ArrayList<Reservation> reservations);
    }
    private static void getAllUsers(DatabaseUserCallback callback) {
        myRef.child("users/Participant").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ArrayList<Participant> temp = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Participant p = child.getValue(Participant.class);
                    if (p != null) temp.add(p);
                }
                callback.onParticipantsLoaded(temp);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("DatabaseConnection", "Error loading participants", error.toException());
                callback.onParticipantsLoaded(new ArrayList<>());
            }
        });

        myRef.child("users/Organizer").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ArrayList<Organizer> temp = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Organizer o = child.getValue(Organizer.class);
                    if (o != null) temp.add(o);
                }
                callback.onOrganizersLoaded(temp);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("DatabaseConnection", "Error loading organizers", error.toException());
                callback.onOrganizersLoaded(new ArrayList<>());
            }
        });

        myRef.child("users/Admin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ArrayList<Admin> temp = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Admin a = child.getValue(Admin.class);
                    if (a != null) temp.add(a);
                }
                callback.onAdminsLoaded(temp);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("DatabaseConnection", "Error loading admins", error.toException());
                callback.onAdminsLoaded(new ArrayList<>());
            }
        });
    }
    private static void getAllEventCategories(DatabaseEventCategoryCallback callback) {
        myRef.child("categories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ArrayList<EventCategory> temp = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    EventCategory c = child.getValue(EventCategory.class);
                    if (c != null) temp.add(c);
                }
                callback.onEventCategoriesLoaded(temp);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("DatabaseConnection", "Error loading event categories", error.toException());
                callback.onEventCategoriesLoaded(new ArrayList<>());
            }
        });
    }
    private static void getAllEvents(DatabaseEventCallback callback) {
        myRef.child("events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ArrayList<Event> temp = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Event e = child.getValue(Event.class);
                    if (e != null) temp.add(e);
                }
                callback.onEventsLoaded(temp);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("DatabaseConnection", "Error loading events", error.toException());
                callback.onEventsLoaded(new ArrayList<>());
            }
        });
    }
    private static void getAllJoinRequests(DatabaseJoinRequestCallback callback) {
        myRef.child("joinRequests").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ArrayList<JoinRequest> temp = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    JoinRequest e = child.getValue(JoinRequest.class);
                    if (e != null) temp.add(e);
                }
                callback.onJoinRequestsLoaded(temp);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("DatabaseConnection", "Error loading join requests", error.toException());
                callback.onJoinRequestsLoaded(new ArrayList<>());
            }
        });
    }
    private static void getAllReservations(DatabaseReservationCallback callback) {
        myRef.child("reservations").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ArrayList<Reservation> temp = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Reservation e = child.getValue(Reservation.class);
                    if (e != null) temp.add(e);
                }
                callback.onReservationsLoaded(temp);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("DatabaseConnection", "Error loading reservations", error.toException());
                callback.onReservationsLoaded(new ArrayList<>());
            }
        });
    }
    private static void updateAllUsers() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(3);
        Log.d("updateAllUsers", "Starting...");

        getAllUsers(new DatabaseUserCallback() {
            @Override
            public void onParticipantsLoaded(ArrayList<Participant> participants) {
                Log.d("updateAllUsers", "Participants loaded");
                allParticipants = participants;
                participantsLoaded = true;
                latch.countDown();
            }

            @Override
            public void onOrganizersLoaded(ArrayList<Organizer> organizers) {
                allOrganizers = organizers;
                Log.d("updateAllUsers", "Organizers loaded");
                organizersLoaded = true;
                latch.countDown();
            }

            @Override
            public void onAdminsLoaded(ArrayList<Admin> admins) {
                Log.d("updateAllUsers", "Admins loaded");
                allAdmins = admins;
                adminsLoaded = true;
                latch.countDown();
            }
        });

        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new InterruptedException("Timeout while waiting for Firebase data.");
        }
    }
    public static void updateAllEventCategories() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Log.d("updateAllEventCategories", "Starting...");

        getAllEventCategories(new DatabaseEventCategoryCallback() {
            @Override
            public void onEventCategoriesLoaded(ArrayList<EventCategory> categories) {
                Log.d("updateAllEventCategories", "EventCategories loaded");
                allEventCategories = categories;
                eventCategoriesLoaded = true;
                latch.countDown();
            }
        });

        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new InterruptedException("Timeout while waiting for Firebase data.");
        }
        Log.d("EventCategories", String.valueOf(allEventCategories));
    }
    public static void updateAllEvents() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Log.d("updateAllEvents", "Starting...");

        getAllEvents(new DatabaseEventCallback() {
            @Override
            public void onEventsLoaded(ArrayList<Event> events) {
                Log.d("updateAllEvents", "Events loaded");
                allEvents = events;
                eventsLoaded = true;
                latch.countDown();
            }
        });

        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new InterruptedException("Timeout while waiting for Firebase data.");
        }
        Log.d("Events", String.valueOf(allEvents));
    }
    public static void updateAllJoinRequests() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Log.d("updateAllJoinRequests", "Starting...");

        getAllJoinRequests(new DatabaseJoinRequestCallback() {
            @Override
            public void onJoinRequestsLoaded(ArrayList<JoinRequest> joinRequests) {
                Log.d("updateAllJoinRequests", "JoinRequests loaded");
                allJoinRequests = joinRequests;
                joinRequestsLoaded = true;
                latch.countDown();
            }
        });

        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new InterruptedException("Timeout while waiting for Firebase data.");
        }
        Log.d("JoinRequests", String.valueOf(allJoinRequests));
    }
    public static void updateAllReservations() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Log.d("updateAllReservations", "Starting...");

        getAllReservations(new DatabaseReservationCallback() {
            @Override
            public void onReservationsLoaded(ArrayList<Reservation> reservations) {
                Log.d("updateAllReservations", "Reservations loaded");
                allReservations = reservations;
                reservationsLoaded = true;
                latch.countDown();
            }
        });

        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new InterruptedException("Timeout while waiting for Firebase data.");
        }
        Log.d("Reservations", String.valueOf(allReservations));
    }
}
