For developers: Libraries needed to compile this project.

JavaFX libraries were copied from the LTS distribution version 11.0.2
downloaded from https://gluonhq.com/products/javafx/

To avoid multiple uploads of the NGSEPcore_<version>.jar library,
this library is not included in this distribution. Please generate and copy the latest version
of this library following the instructions available in the NGSEPcore module

To execute ngsepfx.Main from Eclipse, in the "Run configurations ..." option add to the VM arguments

--add-modules javafx.base,javafx.controls,javafx.fxml,javafx.graphics,javafx.media

Also, in "Dependencies", remove the javafx jars from the classpath and add to the module path
the external folder with the javafx distribution for your operative system.

The command line should look like this:

java --add-modules javafx.base,javafx.controls,javafx.fxml,javafx.graphics,javafx.media -Dfile.encoding=UTF-8 -p /path/to/javafx-sdk-11.0.2/lib -classpath /path/to/NGSEPguifx/bin:/path/to/NGSEPguifx/lib/NGSEPcore_4.0.0.jar ngsepfx.Main
 