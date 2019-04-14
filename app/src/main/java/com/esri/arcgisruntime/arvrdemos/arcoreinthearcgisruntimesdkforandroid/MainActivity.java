package com.esri.arcgisruntime.arvrdemos.arcoreinthearcgisruntimesdkforandroid;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Switch;

import com.esri.arcgisruntime.layers.ArcGISSceneLayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.loadable.LoadStatusChangedEvent;
import com.esri.arcgisruntime.loadable.LoadStatusChangedListener;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.view.ARCoreMotionDataSource;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.FirstPersonCameraController;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalItem;
import com.esri.arcgisruntime.security.AuthenticationManager;
import com.esri.arcgisruntime.security.DefaultAuthenticationChallengeHandler;
import com.esri.arcgisruntime.security.OAuthConfiguration;
import com.esri.arcgisruntime.symbology.Renderer;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.sceneform.ArSceneView;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity implements GestureDetector.OnDoubleTapListener{
  private static final int PERMISSION_TO_USE_CAMERA = 0;

  private SceneView mSceneView;
  private ArSceneView mArSceneView;
  private Camera camera;
  private FirstPersonCameraController fpcController;
  private ArcGISScene scene;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mSceneView = findViewById(R.id.scene_view);
    // Request camera permissions...
    checkForCameraPermissions();
  }

  //Setup the Scene for Augmented Reality
  private void setUpARScene() {
    Log.i("deb", "setup called");
    // Create scene without a basemap. Background for scene content provided by device camera.
    //mSceneView.setScene(new ArcGISScene());


    // Try displaying a web scene from online
    DefaultAuthenticationChallengeHandler authenticationChallengeHandler = new DefaultAuthenticationChallengeHandler(this);
    AuthenticationManager.setAuthenticationChallengeHandler(authenticationChallengeHandler);

    String itemID = "995d284764b54225b9a2411aada7f144";
    Portal portal = new Portal("https://www.arcgis.com",true);
    PortalItem portalItem = new PortalItem(portal, itemID);

    //final Portal portal = new Portal("https://arcgis.com", true);
    portal.addLoadStatusChangedListener(new LoadStatusChangedListener() {
      @Override
      public void loadStatusChanged(LoadStatusChangedEvent loadStatusChangedEvent) {
        Log.i("deb", "status changed " + portal.getLoadStatus().toString());
        LoadStatus loadStatus = loadStatusChangedEvent.getNewLoadStatus();
        if (loadStatus == LoadStatus.LOADED) {
          Log.i("deb", "logged in!");

          int numLayers = 4;

          Thread t = new Thread(() -> {
          do {
            Log.i("deb", "" + scene.getOperationalLayers().size());
          } while (scene.getOperationalLayers().size() < numLayers);

          // EXTRUSION SHIT
          List<Integer> colors = Arrays.asList(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW);
          List<Integer> switches = Arrays.asList(R.id.redSwitch, R.id.greenSwitch, R.id.purpleSwitch, R.id.orangeSwitch);
          for (int i = 0; i < numLayers; i++) {
            FeatureLayer statesFeatureLayer = (FeatureLayer) scene.getOperationalLayers().get(i);

            final SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, colors.get(i), 1.0f);
            final SimpleFillSymbol fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, colors.get(i), lineSymbol);
            final SimpleRenderer renderer = new SimpleRenderer(fillSymbol);
            // set renderer extrusion mode to absolute height, which extrudes the feature to the specified z-value as flat top
            renderer.getSceneProperties().setExtrusionMode(Renderer.SceneProperties.ExtrusionMode.ABSOLUTE_HEIGHT);
            renderer.getSceneProperties().setExtrusionExpression("[HEIGHT]");
            // set the simple renderer to the feature layer
            statesFeatureLayer.setRenderer(renderer);
            statesFeatureLayer.setRenderingMode(FeatureLayer.RenderingMode.DYNAMIC);

            Switch sswitch = findViewById(switches.get(i));
            sswitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
              // set extrusion properties to either show total population or population density based on flag
              if (isChecked) {
                // multiple population density by 5000 to make data legible
                renderer.getSceneProperties().setExtrusionExpression("[HEIGHT]");
                Log.i("deb", "hi");
              } else {
                // divide total population by 10 to make data legible
                renderer.getSceneProperties().setExtrusionExpression("0");
              }
            });
          }

          FeatureLayer statesFeatureLayer = (FeatureLayer) scene.getOperationalLayers().get(numLayers);

          final SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.WHITE, 1.0f);
          final SimpleFillSymbol fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.WHITE, lineSymbol);
          final SimpleRenderer renderer = new SimpleRenderer(fillSymbol);
          // set renderer extrusion mode to absolute height, which extrudes the feature to the specified z-value as flat top
          renderer.getSceneProperties().setExtrusionMode(Renderer.SceneProperties.ExtrusionMode.ABSOLUTE_HEIGHT);
          renderer.getSceneProperties().setExtrusionExpression("[HEIGHT]");
          // set the simple renderer to the feature layer
          statesFeatureLayer.setRenderer(renderer);
          statesFeatureLayer.setRenderingMode(FeatureLayer.RenderingMode.DYNAMIC);
          // EXTRUSION SHIT END
        });

          t.start();

        } else if (loadStatus == LoadStatus.FAILED_TO_LOAD) {
          Log.i("deb", "Load Error:" + portal.getLoadError().getCause());
        }
      }
    });
    portal.loadAsync();

    scene = new ArcGISScene(portalItem);
    mSceneView.setScene(scene);

    // Enable AR for scene view.
    mSceneView.setARModeEnabled(true);

    // Create our Preview view and set it as the content of our activity.
    mArSceneView = new ArSceneView(this);

    // Create an instance of Camera
    FrameLayout preview = findViewById(R.id.camera_preview);
    preview.removeAllViews();
    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    preview.addView(mArSceneView, params);

    camera = new Camera(38.989, -76.9378, 500, 270, 0, 0);
    fpcController = new FirstPersonCameraController();
    fpcController.setInitialPosition(camera);

    fpcController.setTranslationFactor(500);

    ARCoreMotionDataSource arMotionSource = new ARCoreMotionDataSource(mArSceneView,this);
    fpcController.setDeviceMotionDataSource(arMotionSource);

    fpcController.setFramerate(FirstPersonCameraController.FirstPersonFramerate.BALANCED);
    mSceneView.setCameraController(fpcController);

    // To update position and orientation of the camera with device sensors use:
    arMotionSource.startAll();

    Log.i("deb", "setup finished");

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

  @Override
  public boolean onTouchEvent(MotionEvent ev) {
    Log.i("deb", "" + scene.getOperationalLayers().size());
    // Let the ScaleGestureDetector inspect all events.
    //camera = camera.elevate(100);
    //fpcController.setInitialPosition(camera);
    //setUpARScene();
    return true;
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

  @Override
  public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
    return false;
  }

  @Override
  public boolean onDoubleTap(MotionEvent motionEvent) {
    return false;
  }

  @Override
  public boolean onDoubleTapEvent(MotionEvent motionEvent) {
    return false;
  }
}