package house.thelittlemountaindev.ponwel;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.signature.ObjectKey;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import house.thelittlemountaindev.ponwel.models.Product;

import static android.provider.MediaStore.EXTRA_OUTPUT;

/**
 * Created by Charlie One on 6/21/2018.
 */

public class AddProductActivity extends AppCompatActivity implements View.OnClickListener {

    private StorageReference imageRef;
    ArrayList<String> galleryUrls = new ArrayList<>();
    ArrayList<String> sizesArray = new ArrayList<>();
    ArrayList<String> colorArray = new ArrayList<>();

    private String pId = "";
    private static final String ERR_REQUIRED = "Information necessaire.";
    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2; // Choose an image from gallery code
    private static final int STORAGE_REQUEST = 5;

    public int PROFILE_PIC_ADDED = 0;

    private int currentBackgroundColor = 0xffffffff;

    private Uri outputFileUri;
    private EditText pName, pPrice, pDescription, pQuantity;
    private String mCurrentPhotoPath;
    private Uri profilePicUri;
    private ProgressBar uploadProgressBar;
    private Spinner pCategory;
    private DatabaseReference fbDatabase;
    private FirebaseStorage fbStorage;
    private HorizontalScrollView horizontalScrollView;
    private String productKey;
    private TextView addPicturesTxt;
    private FirebaseUser fbUser;
    private Button btnAddColor, btnAddSize;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        //Get an instance of firebase
        fbDatabase = FirebaseDatabase.getInstance().getReference();
        fbStorage = FirebaseStorage.getInstance();
        fbUser = FirebaseAuth.getInstance().getCurrentUser();

        //..........
        addPicturesTxt = findViewById(R.id.tv_add_pictures);
        pName = findViewById(R.id.et_product_name);
        pPrice = findViewById(R.id.et_product_price);
        pDescription = findViewById(R.id.et_product_desc);
        horizontalScrollView = findViewById(R.id.horizontalScrollV);
        pCategory = findViewById(R.id.sp_product_cat);
        pQuantity = findViewById(R.id.et_product_quantity);
        uploadProgressBar = findViewById(R.id.uploadProgressBar);
        btnAddColor = findViewById(R.id.btn_add_colors);
        btnAddSize = findViewById(R.id.btn_add_sizes);
        Button btnSubmit = findViewById(R.id.btn_submit_product);

        //OnClicks
        addPicturesTxt.setOnClickListener(this);
        btnAddColor.setOnClickListener(this);
        btnAddSize.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(AddProductActivity.this,
              R.array.categories, R.layout.spinner_item);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        pCategory.setAdapter(spinnerAdapter);
        pCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        productKey = fbDatabase.child("Products").push().getKey();
    }


/***************************************************************************************************
 *
 ***************************************** METHODS *************************************************
 *
 **************************************************************************************************/
    private void addPicture() {
    final CharSequence[] items = {"Prennant une photo", "Selectionnant du phone", "Annuler"};
    AlertDialog.Builder builder = new AlertDialog.Builder(AddProductActivity.this);
    builder.setTitle("Ajouter en:");
    builder.setItems(items, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            if (items[i].equals("Prennant une photo")) {
                takeCameraPic(); //Open Camera and return image path

            } else if (items[i].equals("Selectionnant du phone")) {
                Intent pickPictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPictureIntent, SELECT_FILE);
            } else if (items[i].equals("Annulez")) {
                dialogInterface.dismiss();
            }
        }
    });
    builder.show();
}

    private void takeCameraPic() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "Ponwel" + timeStamp + ".jpg";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        mCurrentPhotoPath = storageDir.getAbsolutePath() + "/" + imageFileName;
        File file = new File(mCurrentPhotoPath);
        outputFileUri = Uri.fromFile(file);
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        if (cameraIntent.resolveActivity(getPackageManager()) != null){
            cameraIntent.putExtra(EXTRA_OUTPUT, outputFileUri);
            startActivityForResult(cameraIntent, REQUEST_CAMERA);
        }
    }

    private void verifyThenAdd(){

        String pId = productKey;
        String name = pName.getText().toString();
        String category = pCategory.getSelectedItem().toString();
        String price = pPrice.getText().toString();
        String description = pDescription.getText().toString();
        String picUrl = profilePicUri.toString();
        String sellerId = fbUser.getUid();
        String boutique = "Ponwel"; //TODO  queryBoutiqueName
        int quantity = Integer.parseInt(pQuantity.getText().toString());

        if (PROFILE_PIC_ADDED == 0) {
            Toast.makeText(this, "Ajoutez une image", Toast.LENGTH_SHORT).show();
            return;
        }

        //Check required information
        if (TextUtils.isEmpty(name)){
            pName.setError(ERR_REQUIRED);
            return;
        }

        if (TextUtils.equals(category, "- Categorie -")){
            Toast.makeText(this, "Choisissez une categorie", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(price)){
            pPrice.setError(ERR_REQUIRED);
            return;
        }

        //If you have passed all those tests, then...
        addNewProduct(pId, name, category, price, description, picUrl, sellerId, boutique, quantity);
    }

    private void addNewProduct(String pId, String name, String category, String price, String description, String picUrl, String sellerId, String boutique, int quantity) {
        Product product = new Product(pId, name, category, price, description, picUrl, sellerId, boutique, quantity);

        //Sizes map
    //    HashMap<String, Object> sizeMap = new HashMap<>();
    //    sizeMap.put("sizes", sizesArray);

        //colors map
    //    HashMap<String, Object> colorMap = new HashMap<>();
    //    sizeMap.put("colors", colorArray);

        //Product model
        Map<String, Object> productValues = product.toMap();
        productValues.put("sizes/", sizesArray);
        productValues.put("colors/", colorArray);

        //Map to add ...
        HashMap<String, Object> map = new HashMap<>();
        map.put(productKey, true);

        fbDatabase.child("Galleries").child(productKey).setValue(galleryUrls);
        fbDatabase.child("Users").child(fbUser.getUid()).child("seller_products").child(productKey).updateChildren(map);
        fbDatabase.child("Products").child(productKey).setValue(productValues).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
               addedDialog();
                //Toast.makeText(AddProductActivity.this, "Succes", Toast.LENGTH_SHORT).show();
                //finish();
            }
        });
    }

    private void addedDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Succès");
        builder.setMessage("Voulez vous ajouter un autre produit?");

        builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                startActivity(new Intent(AddProductActivity.this, AddProductActivity.class));
                finish();
            }
        });
        builder.setNegativeButton("Terminer", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                finish();
            }
        });

        builder.show();
    }

    private void addImages(final Bitmap bitmap) {
        uploadProgressBar.setVisibility(View.VISIBLE);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String mainPicName = "profilPic";

        StorageReference storageRef = fbStorage.getReferenceFromUrl("gs://ponwel-2d35c.appspot.com");
        final StorageReference galleryRef = storageRef.child("productImages/" + productKey + "/");
        //giving a diff name to the first chosen pic, to be used as profile pic
        if (PROFILE_PIC_ADDED == 0) {
            imageRef = galleryRef.child(mainPicName + ".jpg");
        }else{
            imageRef = galleryRef.child(timeStamp + ".jpg");
        }


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double status = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                if (status ==100){
                    uploadProgressBar.setVisibility(View.GONE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddProductActivity.this, "Erreur! verifiez votre connexion et ressayez" + e, Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //TODO: save  taskSnapshot.getStorage to Array, to delete image if needed
                if (PROFILE_PIC_ADDED == 0){
                    taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                            public void onSuccess(Uri uri) {
                                profilePicUri = uri;
                            }
                    });
                }else{
                    taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            galleryUrls.add(uri.toString());
                        }
                    });
                }

                /*
                  LinearLayout linearLayout = findViewById(R.id.ll_colors);
                  LinearLayout.LayoutParams layoutParams;

          for (int i = 0; i < colorArray.size(); i++) {
            TextView colorBox = new TextView(ProductDetailActivity.this);
            colorBox.setBackgroundResource(R.drawable.rounded_add_btn);
            colorBox.setId(i);
            layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0,16,16,16);
            layoutParams.height = 46;
            layoutParams.width = 46;
            colorBox.setBackgroundColor(Color.parseColor(colorArray.get(i)));
            linearLayout.addView(colorBox, layoutParams);
                */

                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.add_gallery_LL);
                LinearLayout.LayoutParams layoutParams;

                ImageView gallImage = new ImageView(AddProductActivity.this);
                gallImage.setBackgroundResource(R.drawable.boxed_text_bg);
                //gallImage.setPadding(16,16,16,16);
                gallImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                gallImage.setImageBitmap(bitmap);

                layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(16,16,16,16);
                layoutParams.height = 150;
                layoutParams.width = 150;

                linearLayout.addView(gallImage, layoutParams);

                PROFILE_PIC_ADDED = 1;
                horizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_CAMERA:
                if (resultCode == RESULT_OK) {
                    performCrop(outputFileUri);
/*
                    File file = new File(mCurrentPhotoPath);
                  if (file.exists()) {
                      Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                      addImages(bitmap);
                  }
*/
                }
                break;

            case SELECT_FILE:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                       performCrop(data.getData());
                 /*
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                        //addImages(bitmap);
                        resizeCompress(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/

                    }
                }
                break;

            case UCrop.REQUEST_CROP:
                if (resultCode == RESULT_OK) {
                    Uri resultUri = UCrop.getOutput(data);
                    //Uri imageUri = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                        addImages(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

            default:
                break;
        }
    }

    private void resizeCompress(Bitmap bitmap){
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 380,420, false);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
        addImages(resizedBitmap);

    }

/***************************************************************************************************
*
***************************************** PERMISSIONS **********************************************
*
****************************************************************************************************/
    private void storagePermission(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            addPicture();
        }else{
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case STORAGE_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    addPicture();
                }
        }
    }

    private void performCrop(Uri picUri) {
        UCrop.Options options = new UCrop.Options();
        options.setStatusBarColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
        options.setToolbarColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
        options.setToolbarTitle("Customisation");
        UCrop.of(picUri, Uri.fromFile(new File(getCacheDir(), "cropedImage")))
                .withOptions(options)
                .start(this);
    }

   /* private void pickColor() {
        ColorPickerDialogBuilder
                .with(this)
                .setTitle("Choose color")
                .initialColor(currentBackgroundColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(8)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {

                    }
                })
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        colorArray.add("#" + Integer.toHexString(selectedColor));
                        loadColors();
                    }
                })
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }
*/

    private void loadColors(){
        LinearLayout linearLayout = findViewById(R.id.ll_added_colors);
        LinearLayout.LayoutParams layoutParams;

        linearLayout.removeAllViews();

        if (!colorArray.isEmpty()) {
            for (int i = 0; i < colorArray.size(); i++) {
                TextView colorBox = new TextView(AddProductActivity.this);
                colorBox.setBackgroundResource(R.drawable.rounded_add_btn);
                colorBox.setId(i);
                colorBox.setBackgroundColor(Color.parseColor(colorArray.get(i)));

                layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0,16,16,16);
                layoutParams.height = 46;
                layoutParams.width = 46;

                linearLayout.addView(colorBox, layoutParams);


                colorBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        colorArray.remove(v.getId());
                        loadColors();
                    }
                });
            }
        }

    }

    private void addASize() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_add_size);

        final EditText editTextNum = dialog.findViewById(R.id.et_dialog_add_size);
        showKeyboard();

        Button btnDone = dialog.findViewById(R.id.btn_dialog_add_size);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(editTextNum.getText())) {
                    String data = editTextNum.getText().toString();
                    sizesArray.add(data);
                    loadSizes();
                    dialog.dismiss();
                    closeKeyboard();
                }else {
                    Toast.makeText(AddProductActivity.this, "Ajoutez une taille", Toast.LENGTH_SHORT).show();
                }

            }
        });

        dialog.show();

    }

        private void showKeyboard(){
            InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }

        private void closeKeyboard(){
            InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }

    private void loadSizes(){

        LinearLayout linearLayout = findViewById(R.id.ll_added_sizes);
        LinearLayout.LayoutParams layoutParams;

        linearLayout.removeAllViews();

        if (!sizesArray.isEmpty()) {
            for (int i = 0; i < sizesArray.size(); i++) {
                TextView box = new TextView(AddProductActivity.this);
                box.setBackgroundResource(R.drawable.boxed_text_bg);
                box.setId(i);
                layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, 16, 16, 16);
                box.setPadding(8,8,8,8);
                box.setText(sizesArray.get(i));
                linearLayout.addView(box, layoutParams);

                box.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sizesArray.remove(v.getId());
                        loadSizes();
                    }
                });
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_add_colors:
                //pickColor();
                break;

            case R.id.btn_add_sizes:
                addASize();
                break;

            case R.id.btn_submit_product:
                verifyThenAdd();
                break;

            case R.id.tv_add_pictures:
                storagePermission();
                break;
        }
    }
}
