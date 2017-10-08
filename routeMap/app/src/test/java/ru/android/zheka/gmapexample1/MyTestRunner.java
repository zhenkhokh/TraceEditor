package ru.android.zheka.gmapexample1;

import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.manifest.AndroidManifest;
import org.robolectric.res.Fs;
import org.robolectric.res.FsFile;
import org.robolectric.util.Logger;

	public class MyTestRunner extends RobolectricTestRunner {
	    
	    public MyTestRunner(Class<?> testClass) throws org.junit.runners.model.InitializationError {
	        super(testClass);
	    }

	    @Override
	    protected AndroidManifest getAppManifest(Config config) {
	        String appRoot = "src/main/";
	        String manifestPath = appRoot + "AndroidManifest.xml";
	        String resDir = "src/test/res";
	        String assetsDir = appRoot + "assets";
	        AndroidManifest manifest = createAppManifest(Fs.fileFromPath(manifestPath),
	        		Fs.fileFromPath(resDir),
	        		Fs.fileFromPath(assetsDir),
	        		"ru.android.zheka.gmapexample1");
	        		
	        // Robolectric is already going to look in the  'app' dir ...
	        // so no need to add to package name
	        return manifest;
	    }
	
	  protected AndroidManifest createAppManifest(FsFile manifestFile, FsFile resDir, FsFile assetDir, String packageName) {
		    if (!manifestFile.exists()) {
		      System.out.print("WARNING: No manifest file found at " + manifestFile.getPath() + ".");
		      System.out.println("Falling back to the Android OS resources only.");
		      System.out.println("To remove this warning, annotate your test class with @Config(manifest=Config.NONE).");
		      return null;
		    }

		    System.out.println("Robolectric assets directory: " + assetDir.getPath());
		    System.out.println("   Robolectric res directory: " + resDir.getPath());
		    System.out.println("   Robolectric manifest path: " + manifestFile.getPath());
		    System.out.println("    Robolectric package name: " + packageName);
		    return new AndroidManifest(manifestFile, resDir, assetDir, packageName);
		  }
	}
