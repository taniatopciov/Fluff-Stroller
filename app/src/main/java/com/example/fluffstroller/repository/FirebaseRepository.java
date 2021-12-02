package com.example.fluffstroller.repository;

import com.example.fluffstroller.utils.observer.Subject;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FirebaseRepository {
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public <T extends FirebaseDocument> Subject<T> listenForDocumentChanges(String pathToDocument, Class<T> tClass) {
        Subject<T> subject = new Subject<>();

        firestore.document(pathToDocument)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        subject.notifyObservers(error);
                        return;
                    }
                    if (value != null && value.exists()) {
                        subject.notifyObservers(value.toObject(tClass));
                    } else {
                        subject.notifyObservers((T) null);
                    }
                });

        return subject;
    }

    public <T extends FirebaseDocument> Subject<T> listenForDocumentChanges(String pathToDocument, String typeNameField, Map<String, Class<? extends T>> possibleTypes) {
        Subject<T> subject = new Subject<>();

        firestore.document(pathToDocument)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        subject.notifyObservers(error);
                        return;
                    }
                    if (value != null && value.exists()) {
                        try {
                            Map<String, Object> data = value.getData();
                            if (data == null) {
                                subject.notifyObservers((T) null);
                                return;
                            }

                            String type = (String) data.get(typeNameField);
                            Class<? extends T> tClass = possibleTypes.get(type);

                            subject.notifyObservers(value.toObject(tClass));
                        } catch (Exception e) {
                            subject.notifyObservers(e);
                        }
                    } else {
                        subject.notifyObservers((T) null);
                    }
                });

        return subject;
    }


    public <T extends FirebaseDocument> Subject<T> getDocument(String pathToDocument, String typeNameField, Map<String, Class<? extends T>> possibleTypes) {
        Subject<T> subject = new Subject<>();

        firestore.document(pathToDocument)
                .get()
                .addOnSuccessListener(documentReference -> {
                    if (documentReference.exists()) {
                        try {
                            Map<String, Object> data = documentReference.getData();
                            if (data == null) {
                                subject.notifyObservers((T) null);
                                return;
                            }

                            String type = (String) data.get(typeNameField);
                            Class<? extends T> tClass = possibleTypes.get(type);

                            subject.notifyObservers(documentReference.toObject(tClass));
                        } catch (Exception e) {
                            subject.notifyObservers(e);
                        }
                    } else {
                        subject.notifyObservers((T) null);
                    }
                })
                .addOnFailureListener(subject::notifyObservers);
        return subject;
    }

    public <T extends FirebaseDocument> Subject<T> getDocument(String pathToDocument, Class<T> tClass) {
        Subject<T> subject = new Subject<>();

        firestore.document(pathToDocument)
                .get()
                .addOnSuccessListener(documentReference -> {
                    if (documentReference.exists()) {
                        subject.notifyObservers(documentReference.toObject(tClass));
                    } else {
                        subject.notifyObservers((T) null);
                    }
                })
                .addOnFailureListener(subject::notifyObservers);
        return subject;
    }

    public <T extends FirebaseDocument> Subject<List<T>> getAllDocuments(String pathToCollection, Class<T> tClass) {
        Subject<List<T>> subject = new Subject<>();

        firestore.collection(pathToCollection)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<T> documents = queryDocumentSnapshots.getDocuments().stream().map(documentSnapshot -> documentSnapshot.toObject(tClass)).collect(Collectors.toList());

                    subject.notifyObservers(documents);
                })
                .addOnFailureListener(subject::notifyObservers);
        return subject;
    }

    public <T extends FirebaseDocument> Subject<T> addDocument(String pathToCollection, T documentData) {
        Subject<T> subject = new Subject<>();

        firestore.collection(pathToCollection)
                .add(documentData)
                .addOnSuccessListener(documentReference -> {
                    documentData.id = documentReference.getId();
                    subject.notifyObservers(documentData);
                })
                .addOnFailureListener(subject::notifyObservers);
        return subject;
    }

    public Subject<Boolean> updateDocument(String pathToDocument, Map<String, Object> documentData) {
        Subject<Boolean> subject = new Subject<>();

        firestore.document(pathToDocument)
                .update(documentData)
                .addOnSuccessListener(e -> subject.notifyObservers(true))
                .addOnFailureListener(subject::notifyObservers);

        return subject;
    }

    public Subject<Boolean> deleteDocument(String pathToCollection, String documentId) {
        Subject<Boolean> subject = new Subject<>();

        firestore.collection(pathToCollection)
                .document(documentId)
                .delete()
                .addOnSuccessListener(e -> subject.notifyObservers(true))
                .addOnFailureListener(subject::notifyObservers);

        return subject;
    }
}
