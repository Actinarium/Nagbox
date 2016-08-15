# Nagbox
**Recurring reminders for wayward creatives.**

Nagbox is a small app designed to do one thing: nag you repeatedly when you waste your time and not let you lose the track of it.
Just set up a recurring reminder, enable it when you start slacking, and feel guilty each time it fires.

![Screenshots](https://raw.githubusercontent.com/Actinarium/Nagbox/master/images/screens.png)

---

Actually, this is a really simple app made for three reasons:

1. A sample for my upcoming Medium articles on various Android development and design patterns (e.g. the first one will be on SQLite integration approach);
2. A playground to get familiar with new things (e.g. data binding, N notifications);
3. Actually, I need an app like this myself, and it’s quite possible that I’ll further develop it into a complete full-featured app. Think of it as an MVP developed in under a week.

At the moment the project showcases some best practices for:

* working with a local SQLite database using a `Loader`, `ContentProvider`, `IntentService`, and a couple of helper classes I came up with and will explain in an upcoming Medium article;
* displaying data from the database in a `RecyclerView`;
* all of this done using data binding, including 2-way data binding in Create/Edit dialog;
* Android N friendly stacked notifications;
* doing this all with no 3rd party scaffolding (RxJava, Dagger, Realm etc) — only vanilla SDK and support libraries.

## This app on Google Play

<a href='https://play.google.com/store/apps/details?id=com.actinarium.nagbox&referrer=utm_source%3Dgh-nagbox%26utm_medium%3Dreferral%26utm_term%3Dnagbox-readme'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png' height="72" /></a>

## More from the developer

Be the first to know when I publish new articles, tutorials, libraries and apps:

* Add me to circles on [Google+][gplus]
* Follow me on [Medium][medium]

Also check out [Material Cue][mcue], my flagship app for Android developers. It’s free.

## License

```
Copyright (C) 2016 Actinarium

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

Feel free to fork, study, modify, build, and share this project or derivatives.

If you’re going to distribute a derived app, please be decent and put effort into it, and consider adding attribution (e.g. [the link to this original repo][this]).
Do not re-publish this app with zero or minimum modifications, especially for monetization purposes (such as placing an ad banner). Thank you.

[this]: https://github.com/Actinarium/Nagbox
[gplus]: https://plus.google.com/u/0/+PaulDanyliuk/posts
[medium]: https://medium.com/@Actine
[mcue]: https://play.google.com/store/apps/details?id=com.actinarium.materialcue&referrer=utm_source%3Dgh-nagbox%26utm_medium%3Dreferral%26utm_term%3Dnagbox-readme
