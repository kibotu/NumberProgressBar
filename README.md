## Android NumberProgressBar [![Android CI](https://github.com/kibotu/NumberProgressBar/actions/workflows/android.yml/badge.svg)](https://github.com/kibotu/NumberProgressBar/actions/workflows/android.yml) [![](https://jitpack.io/v/kibotu/NumberProgressBar.svg)](https://jitpack.io/#kibotu/NumberProgressBar) [![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=21) [![Gradle Version](https://img.shields.io/badge/gradle-8.1.1-green.svg)](https://docs.gradle.org/current/release-notes) [![Kotlin](https://img.shields.io/badge/kotlin-2.1.0-green.svg)](https://kotlinlang.org/)


-----

The NumberProgressBar is a bar, slim and sexy (every man wants! ). 

---

### Demo

![NumberProgressBar](http://ww3.sinaimg.cn/mw690/610dc034jw1efyrd8n7i7g20cz02mq5f.gif)


[Download Demo](https://github.com/daimajia/NumberProgressBar/releases/download/v1.0/NumberProgressBar-Demo-v1.0.apk)


### Usage
----

#### Gradle

```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
dependencies {
    implementation 'com.github.kibotu:NumberProgressBar:Tag'
}
```

Use it in your own code:

```xml
<com.daimajia.numberprogressbar.NumberProgressBar
    android:id="@+id/number_progress_bar"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
/>
```

I made some predesign style. You can use them via `style` property.


![Preset color](http://ww1.sinaimg.cn/mw690/610dc034jw1efyslmn5itj20f30k074r.jpg)

Use the preset style just like below:

```xml
<com.daimajia.numberprogressbar.NumberProgressBar
    android:id="@+id/number_progress_bar"
    style="@style/NumberProgressBar_Default"
/>
```

In the above picture, the style is : 

`NumberProgressBar_Default`
`NumberProgressBar_Passing_Green`
`NumberProgressBar_Relax_Blue`
`NumberProgressBar_Grace_Yellow`
`NumberProgressBar_Warning_Red`
`NumberProgressBar_Funny_Orange`
`NumberProgressBar_Beauty_Red`
`NumberProgressBar_Twinkle_Night`

You can get more beautiful color from [kular](https://kuler.adobe.com), and you can also contribute your color style to NumberProgressBar!  

### Build

```sh
`./gradlew clean build` 
```

### Attributes

The **reached area** and **unreached area**:

* color
* height 

The **text area**:

* color
* text size
* visibility
* distance between **reached area** and **unreached area**

The **bar**:

* max progress
* current progress

for example, the default style:

```xml
<com.daimajia.numberprogressbar.NumberProgressBar
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    
    custom:progress_unreached_color="#CCCCCC"
    custom:progress_reached_color="#3498DB"
    
    custom:progress_unreached_bar_height="0.75dp"
    custom:progress_reached_bar_height="1.5dp"
    
    custom:progress_text_size="10sp"
    custom:progress_text_color="#3498DB"
    custom:progress_text_offset="1dp"
    custom:progress_text_visibility="visible"
    
    custom:progress_max="100"
    custom:progress_current="80"
 />
```

# License
```
Copyright Jan Rabe 2024

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
