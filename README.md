# Mobius-my-image
App made in Android studio that lets users take or upload a photo and apply a three point mobius transformation to their image and save the result.

## To do list

- Comment in order to understand the code.
- Add a save button in the toolbar that saves the warped image.
- Add a tooltip when the image warping screen is first shown.
- Create arrows indicating how the transformation is transformed, from point to point.
- Rewrite the "About" page.
- Fix any remaining bugs and test the app for bugs

DONE 

## Bugs

- Whenever an image isn't selected or the app is rotated the current image is deleted and the previous image is shown. This could have unintended consequences for the user.
- If the image is the wrong size the app will show a blank image, lag and lock itself. Solution is to check the image size and resize accordingly.
