Glass OCR
=========

Joe Ellis (ellisjoe)

Scott Shi (sjshi)

Final Project for UChicago CS 23400

Building
--------

This project depends on the [tess-two](https://github.com/rmtheis/tess-two) project.

Once the tess-two project is built it must be accessible (copied or symlinked)
from the libraries directory of this project and the build.gradle file in our
extras directory must be copied to the tess-two project root.

Once this is done the GlassOCR project can be compiled using Android Studio.

Notes
-----

* Translating the pictures into text can benefit from more preprocessing
* The GDK (Glass Development Kit) does not provide many of the standard APIs
  that the Android SDK does, so without a dedicated phone application Glass
  isn't able to easily do things like text or email (which is why Twillio is
  being used to send texts directly from Glass).
