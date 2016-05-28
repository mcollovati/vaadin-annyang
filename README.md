# V-Annyang

Voice commands for Vaadin.
 
V-Annyang lets you trigger your server side commands using your voice; 
simply extend UI and register the commands you need.
   
```java
class MyUI extends UI {

    protected void init(VaadinRequest request) {
        // other java code
        //...   
        
        Annyang.of(this)
            .addCommand("test", params -> Notification.show("You called test command"))
            .addCommand("hide :component", params -> componentsMap.get(params[0]).setVisible(false))
            .addCallback((AnnyangEvents.UnsupportedListener) this::unsupportedFeature)
            .start();
        // other java code
        //...   
   }
}
```

The addon is based on [annyang!](https://www.talater.com/annyang/), a tiny javascript library for SpeechRecognition
and command invocation; the Vaadin component lets you easily attach server side code to the client side command invocation.

For more information about command syntax see **annyang!** 
[Command Object](https://github.com/TalAter/annyang/blob/master/docs/README.md#commands-object) page. 


The addons is written for Java 8 and was tested only on google chrome.


## Online demo

TODO
Try the add-on demo at <url of the online demo>

## Download release

TODO

## Building and running demo

```
git clone https://github.com/mcollovati/vaadin-annyang.git
mvn clean install
cd demo
mvn jetty:run
```

To see the demo, navigate to http://localhost:8080/
 
## Release notes

### Version 1.0-SNAPSHOT
- TODO

## Roadmap

This component is developed as a hobby with no public roadmap or any guarantees of upcoming releases. 
That said, the following features are planned for upcoming releases:

- [Speech KITT](https://github.com/TalAter/SpeechKITT) integration


## Issue tracking

The issues for this add-on are tracked on its github.com page. All bug reports and feature requests are appreciated. 

## Contributions

Contributions are welcome, but there are no guarantees that they are accepted as such. Process for contributing is the following:
- Fork this project
- Create an issue to this project about the contribution (bug or feature) if there is no such issue about it already. Try to keep the scope minimal.
- Develop and test the fix or functionality carefully. Only include minimum amount of code needed to fix the issue.
- Refer to the fixed issue in commit
- Send a pull request for the original project
- Comment on the original issue that you have implemented a fix for it

## License & Author

Add-on is distributed under Apache License 2.0. For license terms, see LICENSE.txt.

# Developer Guide

## Getting started

Here is a simple example on how to try out the add-on component:

```java
        Annyang.of(ui)  // extend UI
            // add commands
            .addCommand("test", params -> Notification.show("You called test command"))
            .addCommand("hide :component", params -> componentsMap.get(params[0]).setVisible(false))
            // add callbacks for various events: unsupported, start, end, error, ...
            .addCallback((AnnyangEvents.UnsupportedListener) this::unsupportedFeature)
            // and finally start speech recognition
            .start()
```

For a more comprehensive example, see src/test/java/org/vaadin/addon/annyang/demo/DemoUI.java

## API

V-Annyang JavaDoc is available online at <...>
