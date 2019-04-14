package com.esri.arcgisruntime.arvrdemos.arcoreinthearcgisruntimesdkforandroid;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Switch;

import com.esri.arcgisruntime.layers.ArcGISSceneLayer;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.view.ARCoreMotionDataSource;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.FirstPersonCameraController;
import com.esri.arcgisruntime.mapping.view.ARCoreMotionDataSource;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.sceneform.ArSceneView;

public class MainActivity extends AppCompatActivity {
  private static final int PERMISSION_TO_USE_CAMERA = 0;

  private SceneView mSceneView;
  private ArSceneView mArSceneView;

  private Switch sRes, sLiq, sLib, sGro;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mSceneView = findViewById(R.id.scene_view);

    sRes = findViewById(R.id.orangeSwitch);
    sLiq = findViewById(R.id.redSwitch);
    sLib = findViewById(R.id.greenSwitch);
    sGro = findViewById(R.id.purpleSwitch);

    sRes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {

        }else{

        }
      }
    });

    sLiq.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {

        }else{

        }
      }
    });

    sLib.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {

        }else{

        }
      }
    });

    sGro.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {

        }else{

        }
      }
    });


      // Request camera permissions...
    checkForCameraPermissions();
  }

  //Setup the Scene for Augmented Reality
  private void setUpARScene() {
    // Create scene without a basemap.  Background for scene content provided by device camera.
    mSceneView.setScene(new ArcGISScene());

    // Add San Diego scene layer.  This operational data will render on a video feed (eg from the device camera).
    mSceneView.getScene().getOperationalLayers().add(new ArcGISSceneLayer("https://tiles.arcgis.com/tiles/Imiq6naek6ZWdour/arcgis/rest/services/San_Diego_Textured_Buildings/SceneServer/layers/0"));

    // Enable AR for scene view.
    mSceneView.setARModeEnabled(true);

    // Create our Preview view and set it as the content of our activity.
    mArSceneView = new ArSceneView(this);

    // Create an instance of Camera
    FrameLayout preview = findViewById(R.id.camera_preview);
    preview.removeAllViews();
    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    preview.addView(mArSceneView, params);

    Camera cameraSanDiego = new Camera(32.707, -117.157, 60, 270, 0, 0);
    FirstPersonCameraController fpcController = new FirstPersonCameraController();
    fpcController.setInitialPosition(cameraSanDiego);

    fpcController.setTranslationFactor(500);

    ARCoreMotionDataSource arMotionSource = new ARCoreMotionDataSource(mArSceneView,this);
    fpcController.setDeviceMotionDataSource(arMotionSource);

    fpcController.setFramerate(FirstPersonCameraController.FirstPersonFramerate.BALANCED);
    mSceneView.setCameraController(fpcController);

    // To update position and orientation of the camera with device sensors use:
    arMotionSource.startAll();
  }

  @Override
  protected void onDestroy() {
    if (mArSceneView != null) mArSceneView.destroy();
    super.onDestroy();
  }

  @Override
  protected void onPause(){
    mSceneView.pause();
    if (mArSceneView != null) mArSceneView.pause();
    super.onPause();
  }

  @Override
  protected void onResume(){
    mSceneView.resume();
    if (mArSceneView != null) {
      try {
        mArSceneView.resume();
      }
      catch(CameraNotAvailableException e){}
    }
    super.onResume();
  }

  /**
   * Determine if we're able to use the camera
   */
  private void checkForCameraPermissions() {
    // Explicitly check for privilege
    if (android.os.Build.VERSION.SDK_INT >= 23) {
      final int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
      if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
        Log.i("MainActivity", "Camera permission granted");
        setUpARScene();

      } else {
        Log.i("MainActivity", "Camera permission not granted, asking ....");
        ActivityCompat.requestPermissions(this,
            new String[] { Manifest.permission.CAMERA },
            PERMISSION_TO_USE_CAMERA);
      }
    }
  }
  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         String[] permissions, int[] grantResults) {
    switch (requestCode) {
      case PERMISSION_TO_USE_CAMERA: {
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          Log.i("MainActivity", "Camera permission granted...");
          setUpARScene();
        } else {
          Log.i("MainActivity", "Camera permission denied...");
        }
        return;
      }
    }
  }
}