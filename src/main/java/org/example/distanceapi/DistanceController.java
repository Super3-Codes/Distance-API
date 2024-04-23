package org.example.distanceapi;


import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
public class DistanceController {
    private final FirebaseApp firebaseApp;
    private final Firestore firestore;


    public DistanceController(FirebaseApp firebaseApp, Firestore firestore) {
        this.firebaseApp = firebaseApp;
        this.firestore = firestore;
    }

    @GetMapping("/locations")
    public ResponseEntity<List<Location>> getAllLocations() {
        // Get a reference to the "locations" collection
        CollectionReference locationsRef = firestore.collection("parking locations");

        // Get all documents within the collection
        ApiFuture<QuerySnapshot> future = locationsRef.get();
        List<Location> locations = new ArrayList<>();

        try {
            // Iterate through the documents
            for (QueryDocumentSnapshot document : future.get().getDocuments()) {
                // Convert each document to a Location object
                Location location = document.toObject(Location.class);

                // Parse latitude and longitude strings to doubles
                double latitude = Double.parseDouble(location.getLat());
                double longitude = Double.parseDouble(location.getLon());

                // Create a Coordinate object
                Coordinate coordinate = new Coordinate(latitude, longitude);
                location.setCoordinate(coordinate);

                locations.add(location);
            }
            return ResponseEntity.ok(locations);
        } catch (InterruptedException | ExecutionException | NumberFormatException e) {
            e.printStackTrace();
            // Handle exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
