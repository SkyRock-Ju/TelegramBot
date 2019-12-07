package Subscriber;

import TelegramBot.WeatherBot;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;

public class DatabaseEdit {
    public static void deleteSubscriber(Firestore firestore, String userID) {
        CollectionReference collectionReference = firestore.collection("subscribers");
        if (collectionReference.listDocuments().iterator().hasNext()) {
            collectionReference.listDocuments().forEach(documentReference -> {
                if (documentReference.getId().equals(userID)){
                    documentReference.delete();
                }
            });
        }
    }

    public static void loadSubscribersFromFirestore(Firestore firestore) {
        CollectionReference collectionReference = firestore.collection("subscribers");
        if (collectionReference.listDocuments().iterator().hasNext()) {
            collectionReference.listDocuments().forEach(documentReference -> {
                documentReference.addSnapshotListener((documentSnapshot, e) -> {
                    assert documentSnapshot != null;
                    Subscriber subscriber = documentSnapshot.toObject(Subscriber.class);
                    WeatherBot.subscribers.put(documentSnapshot.getId(),subscriber);
                });
            });
        }
    }
}
