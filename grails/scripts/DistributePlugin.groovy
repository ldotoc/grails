/*
 * Copyright 2004-2005 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Gant script that handles the creation of Grails plugins
 * 
 * @author Graeme Rocher
 *
 * @since 0.4
 */
import org.codehaus.groovy.grails.commons.GrailsClassUtils as GCU  

appName = ""

Ant.property(environment:"env")   
grailsHome = Ant.antProject.properties."env.GRAILS_HOME"    

includeTargets << new File ( "${grailsHome}/scripts/CreateApp.groovy" )

task ( "default" : "Creates a zip containing a Grails plugin for distribution") {
   distributePlugin()                                                      
}     
                
task(distributePlugin:"Implementation task") {
   
   def pluginFile
   new File("${basedir}").eachFile {
     if(it.name.endsWith("GrailsPlugin.groovy")) {
		pluginFile = it
	 }
   }                   

   if(!pluginFile) Ant.fail("Plugin file not found for plugin project")

   def gcl = new GroovyClassLoader()

   Class pluginClass
   try {
    	pluginClass = gcl.parseClass(pluginFile)   
        def plugin = pluginClass.newInstance()    
		def pluginName = GCU.getLogicalName(pluginClass, "GrailsPlugin")
        Ant.zip(basedir:"${basedir}", destfile:"${basedir}/grails-${pluginName}-${plugin.version}.zip")
   }
   catch(Throwable t) {
     println "Throwable: ${t.message}"
     t.printStackTrace(System.out)
   }
}
