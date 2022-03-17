package house.thelittlemountaindev.ponwel;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.model.AspectRatio;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import house.thelittlemountaindev.ponwel.adapter.ProductGridAdapter;
import house.thelittlemountaindev.ponwel.adapter.ProductsManagementAdapter;
import house.thelittlemountaindev.ponwel.models.Product;

import static android.provider.MediaStore.EXTRA_OUTPUT;

public class BoutiqueProfileActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private List<Product> productList;
    private ProductsManagementAdapter adapter;
    private TextView tvAddProduct, tvChangeProfileImg;
    private String mCurrentPhotoPath;
    private ImageView boutiqueProfileImg;
    private ProgressBar uploadProgressBar;

    private static final String ERR_REQUIRED = "Information necessaire.";
    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2; // Choose an image from gallery code
    private static final int STORAGE_REQUEST = 5;

    private Uri outputFileUri;
    private Uri profilePicUri;
    private FirebaseUser user;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boutique_profile);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        productList = new ArrayList<>();
        user = FirebaseAuth.getInstance().getCurrentUser();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.boutique_recycler);
        adapter = new ProductsManagementAdapter(this, productList);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        boutiqueProfileImg = findViewById(R.id.iv_boutique_image);
        tvChangeProfileImg = findViewById(R.id.tv_edit_shop_image);
        tvChangeProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPicture();
            }
        });
        tvAddProduct = (TextView) findViewById(R.id.tv_add_products);
        tvAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BoutiqueProfileActivity.this, AddProductActivity.class));
            }
        });
        loadProducts();
    }

    private void loadProducts(){
        //databaseReference.child("Users").child(user.getUid()).child("products").addValueEventListener(new ValueEventListener() {
        databaseReference.child("Products").orderByChild("product_quantity").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();
                if (dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        productList.add(snapshot.getValue(Product.class));
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addPicture() {
        final CharSequence[] items = {"Prennant une photo", "Selectionnant du phone", "Annuler"};
        AlertDialog.Builder builder = new AlertDialog.Builder(BoutiqueProfileActivity.this);
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
                        uploadImage(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

            default:
                break;
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

    private void uploadImage(final Bitmap bitmap) {
        String mainPicName = "BoutiqueProfilPic";

        StorageReference storageRef = firebaseStorage.getReferenceFromUrl("gs://ponwel-2d35c.appspot.com");
        final StorageReference galleryRef = storageRef.child("boutique/" + user.getUid() + "/");
        //giving a diff name to the first chosen pic, to be used as profile pic
        StorageReference imageRef;
            imageRef = galleryRef.child(mainPicName + ".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(BoutiqueProfileActivity.this, "Erreur! verifiez votre connexion et ressayez" + e, Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        profilePicUri = uri;
                        boutiqueProfileImg.setImageBitmap(bitmap);
                    }
                });
            }
        });


    }

}
