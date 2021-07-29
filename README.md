# AndroidMNISTNumberRecognition
Android application that recognize MNIST Handwritten Digits. This is using a Convolutional Neural Network (CNN) model I made and trained in PyTorch.  

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
:white_check_mark: 99% overall accuracy.

## Screenshots (TBU)
<img src="/images/example_4.jpg" alt="example_4" width="300"/> <img src="/images/example_9.jpg" alt="example_9" width="300"/>

## Areas of improvement
- Update `torch` and `torchvision`
- Update `jcenter()` to `mavenCentral()`
- Add an app icon
- Improve Light / Dark Mode UI
- Add comments to the code
- Improve `README.md`
