package com.example.fluffstroller.pages.profile.dogowner;

import android.graphics.Bitmap;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AddDogViewModel extends ViewModel {
    private final MutableLiveData<String> name;
    private final MutableLiveData<String> breed;
    private final MutableLiveData<String> age;
    private final MutableLiveData<String> description;
    private final MutableLiveData<Bitmap> bitmap;

    public AddDogViewModel() {
        name = new MutableLiveData<>();
        breed = new MutableLiveData<>();
        age = new MutableLiveData<>();
        description = new MutableLiveData<>();
        bitmap = new MutableLiveData<>();
    }

    public MutableLiveData<String> getBreed() {
        return breed;
    }

    public MutableLiveData<String> getAge() {
        return age;
    }

    public MutableLiveData<String> getDescription() {
        return description;
    }

    public MutableLiveData<Bitmap> getBitmap() {
        return bitmap;
    }

    public void setName(String name) {
        this.name.postValue(name);
    }

    public void setAge(String age) {
        this.age.postValue(age);
    }

    public void setBreed(String breed) {
        this.breed.postValue(breed);
    }

    public void setDescription(String description) {
        this.description.postValue(description);
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap.postValue(bitmap);
    }
}