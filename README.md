To run this app, download and import into Android Studioo and run.  Start using the app by downloading images using the download button.
Then, click the load image button and select an image from the gallery.
Select a brush (square by default) and draw on the right side of the screen.  The invert colors box, when checked, 
will invert colors taken from the image until it is unchecked.  The Click Draw box will randomly generate locations to draw using
the currently selected brush for each tap of the drawing area on the screen.  The amount of locations being drawn is dependent on
the brush (dots draws 3000 pixels per tap, line draws 400 lines per tap).
Saving an image stores it at the end of the gallery.  Clear will clear the drawing on the right and leave the image untouched.
The dots brush does not support non-click draw and the velocity brushes do not support click draw.

The app was tested using a Nexus 10 API 24.

References:
Android developer documentation, including Canvas, gesture movement (for velocity tracking), VelocityTracker, Bitmap, checkbox, and Color.
https://developer.android.com/reference/android/

Stack Overflow, including:
http://stackoverflow.com/questions/7807360/how-to-get-pixel-colour-in-android;
http://stackoverflow.com/questions/8560501/android-save-image-into-gallery;
http://stackoverflow.com/questions/19834842/android-gallery-on-kitkat-returns-different-uri-for-intent-action-get-content;
http://stackoverflow.com/questions/30485073/clear-bitmap-in-android;
http://stackoverflow.com/questions/10155907/speed-or-acceleration-of-motion-event-android;
http://stackoverflow.com/questions/5253681/how-do-i-make-my-android-app-generate-a-random-number;
http://stackoverflow.com/questions/18141976/how-to-invert-an-rgb-color-in-integer-form

Dr. Jon Froelich and past course award winners for inspiration.
