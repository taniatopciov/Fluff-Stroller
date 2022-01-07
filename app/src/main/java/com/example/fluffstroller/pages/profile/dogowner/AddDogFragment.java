package com.example.fluffstroller.pages.profile.dogowner;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.fluffstroller.R;
import com.example.fluffstroller.databinding.AddDogFragmentBinding;
import com.example.fluffstroller.models.Dog;
import com.example.fluffstroller.utils.components.CustomToast;
import com.example.fluffstroller.utils.formatting.TimeIntegerTextWatcher;
import com.example.fluffstroller.utils.photoUpload.FileUtil;

import java.util.function.Consumer;

public class AddDogFragment extends Fragment {

    private enum Actions {NONE, CAMERA, GALLERY}

    private AddDogFragmentBinding binding;
    private AddDogViewModel viewModel;

    private Consumer<Bitmap> bitmapConsumer;
    private Actions action;

    private ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent intent = result.getData();
                        if (action.equals(Actions.CAMERA)) {
                            if (bitmapConsumer != null) {
                                Bitmap takenImage = (Bitmap) intent.getExtras().get("data");
                                bitmapConsumer.accept(takenImage);
                            }
                        } else if (action.equals(Actions.GALLERY)) {
                            try {
                                Uri selectedImageUri = intent.getData();
                                bitmapConsumer.accept(FileUtil.bitmapFrom(getContext(), selectedImageUri));
                            } catch (Exception e) {
                                bitmapConsumer.accept(null);
                                e.printStackTrace();
                            }
                        }
                    } else {
                        if (bitmapConsumer != null) {
                            bitmapConsumer.accept(null);
                        }
                    }
                    bitmapConsumer = null;
                    action = Actions.NONE;
                }
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = AddDogFragmentBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(AddDogViewModel.class);

        binding.ageTextWithLabelAddDogFragment.addTextChangedListener(new TimeIntegerTextWatcher(binding.ageTextWithLabelAddDogFragment.editText, "years"));

        viewModel.getBitmap().observe(getViewLifecycleOwner(), bitmap -> binding.dogImageAddDogFragment.setImageBitmap(bitmap));

        binding.addPhotoButtonDogCard.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(getContext(), view);
            popupMenu.inflate(R.menu.upload_photo_popup_menu);
            popupMenu.setOnMenuItemClickListener(menuItem -> {

                if (menuItem.getItemId() == R.id.uploadFromCameraMenuItem) {
                    launchCamera(bitmap -> {
                        if (bitmap != null) {
                            viewModel.setBitmap(bitmap);
                        } else {
                            CustomToast.show(requireActivity(), "Picture not taken!",
                                    Toast.LENGTH_LONG);
                        }
                    });
                    return true;
                }

                if (menuItem.getItemId() == R.id.uploadFromGalleryMenuItem) {
                    openGallery(bitmap -> {
                        if (bitmap != null) {
                            viewModel.setBitmap(bitmap);
                        } else {
                            CustomToast.show(requireActivity(), "Picture not selected!",
                                    Toast.LENGTH_LONG);
                        }
                    });
                    return true;
                }
                return false;
            });
            popupMenu.show();
        });

        binding.saveButtonAddDogFragment.setOnClickListener(view -> {
            String name = binding.nameTextWithLabelAddDogFragment.getText();
            String breed = binding.breedTextWithLabelAddDogFragment.getText();
            String ageString = binding.ageTextWithLabelAddDogFragment.getText();
            int age = extractInteger(ageString);
            String description = binding.descriptionTextWithLabelAddDogFragment.getText();
            Bitmap dogImage = viewModel.getBitmap().getValue();

            if (name.isEmpty() || breed.isEmpty() || ageString.isEmpty()) {
                CustomToast.show(requireActivity(), "Name, breed and age must be completed!",
                        Toast.LENGTH_LONG);
                return;
            }

            Dog dog;
            if (dogImage != null) {
                dog = new Dog(name, breed, age, description, dogImage);
            } else {
                dog = new Dog(name, breed, age, description);
            }

            AddDogFragmentDirections.ActionFromAddDogToEditOwnerProfile addDogToEdit = AddDogFragmentDirections.actionFromAddDogToEditOwnerProfile(dog);
            Navigation.findNavController(view).navigate(addDogToEdit);
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private int extractInteger(String input) {
        if (input == null || input.isEmpty()) {
            return 0;
        }

        try {
            String[] parts = input.split(" ");
            if (parts.length == 2) {
                return Integer.parseInt(parts[0]);
            }

        } catch (NumberFormatException e) {
            return 0;
        }

        return 0;
    }

    public void launchCamera(Consumer<Bitmap> onImageTaken) {
        this.bitmapConsumer = onImageTaken;
        action = Actions.CAMERA;

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        activityResultLauncher.launch(intent);
    }

    public void openGallery(Consumer<Bitmap> onImageSelected) {
        this.bitmapConsumer = onImageSelected;
        action = Actions.GALLERY;

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }
}