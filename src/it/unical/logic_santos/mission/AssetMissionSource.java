/**
 * 
 */
package it.unical.logic_santos.mission;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author Agostino
 *
 */
public class AssetMissionSource implements IMissionSource {

	private String folderSourcePath=null;
	
	public AssetMissionSource( final String folderSourcePath ) {
		this.folderSourcePath = folderSourcePath;
	}
	
	/*public List< String > getAvailableMissionClassNames() {
		File folder = new File( folderSourcePath );
		File[] files = folder.listFiles();
		List< String > availableMissionClassNames = new ArrayList< String >();
		
		for( int i=0; i<files.length; ++i )
			if ( files[i].isFile() ) {
				
				String fileName = files[i].getName();
				if ( isClassFile( fileName ) )
					availableMissionClassNames.add( files[i].getAbsolutePath() );
			}
	
		return availableMissionClassNames;
	}*/
	
	@SuppressWarnings("unchecked")
	public List< Class< IMission > > getAvailableMissionClasses() {
		File folder = new File( folderSourcePath );
		File[] files = folder.listFiles();
		List< Class< IMission > > availableMissionClasses = new ArrayList< Class< IMission> >();
		
		for( int i=0; i<files.length; ++i )
			if ( files[i].isFile() ) {
				
				String fileName = files[i].getName();
				if ( isJarFile( fileName ) ) {
					
					try {
						
						final String pathToJar = files[i].getAbsolutePath();
						JarFile jarFile = new JarFile( pathToJar );
						Enumeration< JarEntry > jarEntries = jarFile.entries();
						
						URL[] urls = { new URL( "jar:file:"+pathToJar+"!/" ) };
						URLClassLoader classLoader = URLClassLoader.newInstance( urls );
						
						while( jarEntries.hasMoreElements() ) {
							
							JarEntry jarEntry = jarEntries.nextElement();
							
							if ( (!jarEntry.isDirectory()) && ( isClassFile( jarEntry.getName() ) ) ) {
								String className = getClassName( jarEntry.getName() );
								className=className.replace( '/', '.' );
								
								try {
									/* load the Mission class */
									System.out.println(" CLASS NAME: "+className);
									Class< IMission > c = ( Class< IMission > ) classLoader.loadClass( className );
									availableMissionClasses.add( c );
								}  catch (ClassNotFoundException e) {
									e.printStackTrace();
								}
							}
						}
						
						jarFile.close();
						
					} catch (IOException e) {
						e.printStackTrace();
					}
				
				}
			}
	
		return availableMissionClasses;
	}
	
	private static boolean isClassFile( final String fileName ) {
		return ( fileName.endsWith( ".class" ) );
	}
	
	private static boolean isJarFile( final String fileName ) {
		return ( fileName.endsWith( ".jar" ) );
	}
	
	private static String getClassName( final String classFileName ) {
		return ( classFileName.substring( 0, classFileName.length()-6) );
	}
}
