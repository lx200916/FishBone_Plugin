package com.github.lx200916.fishboneplugin.services

import com.intellij.openapi.project.Project
import com.github.lx200916.fishboneplugin.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
