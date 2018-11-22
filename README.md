querydslsbtplugin
=================

QueryDSL entity generation for [Play 2.x](http://www.playframework.com) and sbt version [1.x] .Implemented as [sbt](http://www.scala-sbt.org)
plugin. 


Installation
-------------------

Add following to your projects `project/plugins.sbt`

	addSbtPlugin("com.dayslar.play" % "querydslsbtplugin" % "0.1.1")
	
In Play >= 2.3 you will be able to benefit from `AutoPlugin` mecanism from SBT, 
this way you only need to add to your project the `enablePlugins(QueryDSLPlugin)` magic.

In addition you'll need to add settings to your project. On Play 2.2 this is
done by modifying `build.sbt` and adding settings for the QueryDSLPlugin as
well as adding its dependency scope.


	playJavaSettings ++ QueryDSLPlugin.queryDSLSettings

    val current = project.in(file(".")).configs(QueryDSLPlugin.QueryDSL)

On Play 2.1 and Play 2.0 you should do following changes to `project/Build.scala`.

    val main = play.Project(appName, appVersion, appDependencies,
                            settings = playJavaSettings ++ QueryDSLPlugin.queryDSLSettings)
                            .settings(
                            // your settings
                            )
                            .configs(QueryDSLPlugin.QueryDSL)

This adds QueryDSL support for JPA entities in your PlayFramework! project.
Please note that EBeans should no longer be used or it will lead to compilation errors (and it does not make sense anyway).


Settings
--------

By default, the plugin scans for changes for classes in the `models` package (according to the default directory layout of PlayFramework!).
If you want to customize this to allow scanning in another package, you will have to set the plugin property `QueryDSLPlugin.queryDSLPackage`
to the correct value in your project settings

    //From Play 2.3 and onward (thanks to autoImport feature)
    queryDSLPackage := "com/dayslar/db/entity"
    //OR Up to play 2.2
    QueryDSLPlugin.queryDSLPackage := "com/dayslar/db/entity"

Versions
--------

* **0.1.1** [2018-11-19]
    Initial release

Acknowledgements
----------------

This plugin is based on [play-querydsl](https://github.com/CedricGatay/play-querydsl) plugin
and QueryDSL ant configuration recommendation.

License
-------

    Copyright 2018 Dayslar

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
