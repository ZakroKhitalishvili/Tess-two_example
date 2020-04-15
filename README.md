# Tess-two_example
This is a forked version for Tess-two example ([apk in Google Play Market](https://play.google.com/store/apps/details?id=com.ashomok.tesseractsample)
![alt tag](http://s32.postimg.org/dzcyc1fet/image.png))

# Changes
Dependencies updated, replaced deprecated code.
Added support for Tesseract OCR to process Georgian texts.
Added detailed result showing confidence, elapsed time and found text components on an image.


## Usefull info
##### What I need to start use Tesseract classes in my Android project:
add to ```build.gradle```:

```
dependencies {
    compile 'com.rmtheis:tess-two:9.1.0'
}
```

ანდროიდის სანიმუშო პროექტი სურათებზე ქართული ტექსტების ამოცნობისთვის(OCR), რმელიც იყენებს Tesseract-ზე დაფუძნებული Tess-two ბიბლიოთეკას.
