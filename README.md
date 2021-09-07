# AndroidMNISTNumberRecognition
Android application that recognize MNIST Handwritten Digits. This is using a Convolutional Neural Network (CNN) model I made and trained with [PyTorch](https://pytorch.org/).  

## Download
- You can clone this repo using the following command and then open the project in [Android Studio](https://developer.android.com/studio). You will then have the possibility to compile the app and launch it on your own Android device or on an emulator.
```
git clone https://github.com/BenoitBrebion/AndroidMNISTNumberRecognition.git
```

- Or you can directly download the latest APK file available in the [Releases](https://github.com/BenoitBrebion/AndroidMNISTNumberRecognition/releases) tab and run it on your Android device.

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
<img src="/images/example_3.jpg" alt="example_3" width="300"/> <img src="/images/example_8.jpg" alt="example_8" width="300"/>
