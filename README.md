#  Android MNIST Digits Recognition
Android application that recognise MNIST Handwritten Digits. This is using a Convolutional Neural Network (CNN) model I made and trained with [PyTorch](https://pytorch.org/).  
This app allows you to recognise images of MNIST Handwritten Digits taken from the [official dataset](http://yann.lecun.com/exdb/mnist/) (but not used during model training). You can also draw your own digits and have them recognised by the model.

## Download
- You can clone the latest release of this repo using the following command and then open the project in [Android Studio](https://developer.android.com/studio). You will then have the possibility to compile the app and launch it on your own Android device or on an emulator.
```
git clone https://github.com/BenoitBrebion/android-mnist-digits-recognition.git -b v1.4.0
```

- Or you can directly download the latest APK file available in the [Releases](https://github.com/BenoitBrebion/android-mnist-digits-recognition/releases) tab and run it on your Android device.

## Model
```
CNN(  
    (conv1): Conv2d(1, 8, kernel_size=(3, 3), stride=(1, 1), padding=(1, 1))  
    (conv2): Conv2d(8, 16, kernel_size=(3, 3), stride=(1, 1), padding=(1, 1))  
    (pool): MaxPool2d(kernel_size=2, stride=2, padding=0, dilation=1, ceil_mode=False)  
    (fc1): Linear(in_features=784, out_features=400, bias=True)  
    (fc2): Linear(in_features=400, out_features=10, bias=True)  
    (dropout): Dropout(p=0.25, inplace=False)  
)
```
:white_check_mark: 99% overall accuracy (loss: 0.025208).

## Screenshots
### Recognition
<img src="/images/recognition_4.jpg" alt="recognition_4" width="300"/> <img src="/images/recognition_5_dark.jpg" alt="recognition_5_dark" width="300"/>
### Draw
<img src="/images/draw_3.jpg" alt="draw_3" width="300"/> <img src="/images/draw_7.jpg" alt="draw_7" width="300"/>
