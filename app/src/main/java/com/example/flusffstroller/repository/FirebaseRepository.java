package com.example.flusffstroller.repository;

import com.example.flusffstroller.utils.observer.Subject;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FirebaseRepository {
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public <T> Subject<T> getDocument(String pathToDocument, Class<T> tClass) {
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

    public <T> Subject<List<T>> getAllDocuments(String pathToCollection, Class<T> tClass) {
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

    public <T> Subject<String> addDocument(String pathToCollection, T documentData) {
        Subject<String> subject = new Subject<>();

        firestore.collection(pathToCollection)
                .add(documentData)
                .addOnSuccessListener(documentReference -> subject.notifyObservers(documentReference.getId()))
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
