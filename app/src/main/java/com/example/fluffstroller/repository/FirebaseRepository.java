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
                .addSnapshotListener((documentReference, error) -> {
                    try {
                        if (error != null) {
                            subject.notifyObservers(error);
                            return;
                        }
                        if (documentReference != null && documentReference.exists()) {
                            T object = documentReference.toObject(tClass);
                            if (object != null) {
                                object.setId(documentReference.getId());
                            }
                            subject.notifyObservers(object);
                        } else {
                            subject.notifyObservers((T) null);
                        }
                    } catch (Exception e) {
                        subject.notifyObservers(e);
                    }
                });

        return subject;
    }

    public <T extends FirebaseDocument> Subject<T> listenForDocumentChanges(String pathToDocument, String typeNameField, Map<String, Class<? extends T>> possibleTypes) {
        Subject<T> subject = new Subject<>();

        firestore.document(pathToDocument)
                .addSnapshotListener((documentReference, error) -> {
                    try {
                        if (error != null) {
                            subject.notifyObservers(error);
                            return;
                        }
                        if (documentReference != null && documentReference.exists()) {

                            Map<String, Object> data = documentReference.getData();
                            if (data == null) {
                                subject.notifyObservers((T) null);
                                return;
                            }

                            String type = (String) data.get(typeNameField);
                            Class<? extends T> tClass = possibleTypes.get(type);

                            T object = documentReference.toObject(tClass);
                            if (object != null) {
                                object.setId(documentReference.getId());
                            }
                            subject.notifyObservers(object);

                        } else {
                            subject.notifyObservers((T) null);
                        }
                    } catch (Exception e) {
                        subject.notifyObservers(e);
                    }
                });

        return subject;
    }


    public <T extends FirebaseDocument> Subject<T> getDocument(String pathToDocument, String typeNameField, Map<String, Class<? extends T>> possibleTypes) {
        Subject<T> subject = new Subject<>();

        firestore.document(pathToDocument)
                .get()
                .addOnSuccessListener(documentReference -> {
                    try {
                        if (documentReference.exists()) {
                            Map<String, Object> data = documentReference.getData();
                            if (data == null) {
                                subject.notifyObservers((T) null);
                                return;
                            }

                            String type = (String) data.get(typeNameField);
                            Class<? extends T> tClass = possibleTypes.get(type);

                            T object = documentReference.toObject(tClass);
                            if (object != null) {
                                object.setId(documentReference.getId());
                            }
                            subject.notifyObservers(object);

                        } else {
                            subject.notifyObservers((T) null);
                        }
                    } catch (Exception e) {
                        subject.notifyObservers(e);
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
                    try {
                        if (documentReference.exists()) {
                            T object = documentReference.toObject(tClass);
                            if (object != null) {
                                object.setId(documentReference.getId());
                            }
                            subject.notifyObservers(object);
                        } else {
                            subject.notifyObservers((T) null);
                        }
                    } catch (Exception e) {
                        subject.notifyObservers(e);
                    }
                })
                .addOnFailureListener(subject::notifyObservers);
        return subject;
    }

    public <T extends FirebaseDocument> Subject<List<T>> listenForCollectionChanges(String pathToCollection, Class<T> tDocumentClass) {
        Subject<List<T>> subject = new Subject<>();

        firestore.collection(pathToCollection)
                .addSnapshotListener((value, error) -> {
                    try {
                        if (error != null) {
                            subject.notifyObservers(error);
                            return;
                        }
                        if (value == null) {
                            subject.notifyObservers((List<T>) null);
                            return;
                        }

                        List<T> documents = value.getDocuments().stream().map(documentSnapshot -> {
                            T object = documentSnapshot.toObject(tDocumentClass);
                            if (object != null) {
                                object.setId(documentSnapshot.getId());
                            }
                            return object;
                        }).collect(Collectors.toList());

                        subject.notifyObservers(documents);
                    } catch (Exception e) {
                        subject.notifyObservers(e);
                    }
                });

        return subject;
    }

    public <T extends FirebaseDocument> Subject<List<T>> getAllDocuments(String pathToCollection, Class<T> tClass) {
        Subject<List<T>> subject = new Subject<>();

        firestore.collection(pathToCollection)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    try {
                        List<T> documents = queryDocumentSnapshots.getDocuments().stream().map(documentSnapshot -> {
                            T object = documentSnapshot.toObject(tClass);
                            if (object != null) {
                                object.setId(documentSnapshot.getId());
                            }
                            return object;
                        }).collect(Collectors.toList());

                        subject.notifyObservers(documents);
                    } catch (Exception e) {
                        subject.notifyObservers(e);
                    }
                })
                .addOnFailureListener(subject::notifyObservers);
        return subject;
    }

    public <T extends FirebaseDocument> Subject<T> addDocument(String pathToCollection, T documentData) {
        Subject<T> subject = new Subject<>();

        firestore.collection(pathToCollection)
                .add(documentData)
                .addOnSuccessListener(documentReference -> {
                    try {
                        documentData.setId(documentReference.getId());
                        subject.notifyObservers(documentData);
                    } catch (Exception e) {
                        subject.notifyObservers(e);
                    }
                })
                .addOnFailureListener(subject::notifyObservers);
        return subject;
    }

    public <T extends FirebaseDocument> Subject<T> setDocument(String pathToCollection, String documentId, T documentData) {
        Subject<T> subject = new Subject<>();

        firestore.collection(pathToCollection)
                .document(documentId)
                .set(documentData)
                .addOnSuccessListener(documentReference -> {
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
