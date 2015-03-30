package org.nfalco.tools.ant.taskdefs.test;

import static org.junit.Assert.*;
import static org.ops4j.pax.tinybundles.core.TinyBundles.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Manifest;
import org.apache.tools.ant.taskdefs.Manifest.Attribute;
import org.apache.tools.ant.types.FileSet;
import org.junit.Test;
import org.nfalco.tools.ant.taskdefs.ApplicationConstants;
import org.nfalco.tools.ant.taskdefs.EBA;

import aQute.bnd.osgi.Constants;

public class EBATest {

	private static final String BUNDLE_SYMBOLICNAME = "eba.bundle.test";
	private static final String BUNDLE_VERSION = "1.0.0";
	private static final String WAB_SYMBOLICNAME = "eba.web.bundle.test";
	private static final String WAB_VERSION = "2.0.0";

	private File createBundle() throws IOException {
		InputStream is = bundle()
				.add(EBA.class)
				.add(EBATest.class)
				.set(Constants.BUNDLE_MANIFESTVERSION, "2")
				.set(Constants.BUNDLE_VERSION, BUNDLE_VERSION)
				.set(Constants.BUNDLE_SYMBOLICNAME, BUNDLE_SYMBOLICNAME)
				.set(Constants.EXPORT_PACKAGE, EBA.class.getPackage().getName())
				.build();

		File bundleFile = File.createTempFile("bundle", ".jar");
		bundleFile.deleteOnExit();
		FileUtils.copyInputStreamToFile(is, bundleFile);

		return bundleFile;
	}

	private File createWebBundle() throws IOException {
		InputStream is = bundle()
				.add(EBA.class)
				.add(EBATest.class)
				.set(Constants.BUNDLE_MANIFESTVERSION, "2")
				.set(Constants.BUNDLE_VERSION, WAB_VERSION)
				.set(Constants.BUNDLE_SYMBOLICNAME, WAB_SYMBOLICNAME)
				.set(Constants.EXPORT_PACKAGE, EBA.class.getPackage().getName())
				.set("Web-ContextPath", "/test")
				.build();

		File bundleFile = File.createTempFile("bundle-web", ".jar");
		bundleFile.deleteOnExit();
		FileUtils.copyInputStreamToFile(is, bundleFile);

		return bundleFile;
	}

	@Test
	public void eba() throws Exception {
		String ebaSymbolicName = "org.nfalco.eba.sample";
		String ebaVersion = "1.0.0.110";

		File ebaFile = File.createTempFile("test", ".eba");
		ebaFile.delete();

		Project project = new Project();

		EBA task = new EBA();
		task.setProject(project);
		task.setDestFile(ebaFile);
		task.setSymbolicName(ebaSymbolicName);
		task.setVersion(ebaVersion);

		File bundle = createBundle();
		FileSet fileSet = new FileSet();
		fileSet.setProject(project);
		fileSet.setFile(bundle);

		File webBundle = createWebBundle();
		FileSet externalContent = new FileSet();
		externalContent.setProject(project);
		externalContent.setFile(webBundle);

		task.add(fileSet);
		task.addExtraFileSet(externalContent);
		try {
			task.execute();

			assertTrue(ebaFile.exists());
			assertTrue(ebaFile.length() > 0);

			ZipFile zf = new ZipFile(ebaFile);
			ZipEntry ze = zf.getEntry(bundle.getName());
			assertNotNull(ze);

			ze = zf.getEntry("META-INF/APPLICATION.MF");
			assertNotNull(ze);

			InputStreamReader reader = new InputStreamReader(zf.getInputStream(ze));
			Manifest mf = new Manifest(reader);
			reader.close();

			Attribute attribute = mf.getMainSection().getAttribute(ApplicationConstants.APPLICATION_MANIFEST_VERSION);
			assertNotNull(attribute);
			assertEquals("1.0", attribute.getValue());

			attribute = mf.getMainSection().getAttribute(ApplicationConstants.APPLICATION_SYMBOLIC_NAME);
			assertNotNull(attribute);
			assertEquals(ebaSymbolicName, attribute.getValue());

			attribute = mf.getMainSection().getAttribute(ApplicationConstants.APPLICATION_VERSION);
			assertNotNull(attribute);
			assertEquals(ebaVersion, attribute.getValue());

			attribute = mf.getMainSection().getAttribute(ApplicationConstants.APPLICATION_CONTENT);
			assertNotNull(attribute);
			assertTrue(attribute.getValue().contains(WAB_SYMBOLICNAME + ";version=\"" + WAB_VERSION + "\""));
			assertTrue(attribute.getValue().contains(BUNDLE_SYMBOLICNAME + ";version=\"" + BUNDLE_VERSION + "\""));

			IOUtils.closeQuietly(zf);
		} finally {
			if (!ebaFile.delete()) {
				ebaFile.deleteOnExit();
			}
		}
	}
}