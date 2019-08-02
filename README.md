# Harrastuspassi Android

> Harrastuspassi project consists of Android and iOS mobile applications, a web based admin user interface and a backend system. This is the repository for the Android application.

[![Kotlin Version][kotlin-image]][kotlin-url]

## Requirements

- Android Studio 3.4+
- Android SDK 24+

## Setting up development environment

Clone this project to your local machine using `git clone`.

To get the project up and running you'll need to create:

`/app/src/main/res/values/api_keys.xml`

and add the following contents with an API URL and a valid Google Maps SDK API-key:

```
# api_keys.xml

<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="GOOGLE_MAPS_API_KEY" translatable="false">{YOUR_API_KEY}</string>
    <string name="API_URL" translatable="false">{YOUR_API_URL}</string>
</resources>
```

If android studio doesn't automatically create a `local.properties` file at your project root, create it and insert your SDK path:
```
# local.properties

sdk.dir=<PATH_TO_YOUR_SDK>
```

After this you'll be able to run the project via Android Studio. :sunglasses:

## Related projects

You can view the Harrastuspassi project's main repository, with links to other related projects [here.](https://github.com/City-of-Helsinki/harrastuspassi)

[kotlin-image]: https://img.shields.io/badge/Kotlin-1.3.30-orange.svg
[kotlin-url]: https://kotlinlang.org/


