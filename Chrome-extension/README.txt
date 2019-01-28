This folder contains the components that you need to run the Chrome extension of ExampleStack. Here is a short description.

1. The chrome directory contains the source code and resources of the Chrome extension. 

2. The sample-dataset directory contains seven groups of SO code examples and their GitHub counterparts to demonstrate the tool features of ExampleStack.

3. The So-posts directory contains the contents of Stack Overflow posts that have clones in GitHub. The post contents will be used to compute the offsets to inject highlighting html tags.

4. examplestack-keystore is the keystore for the SSL certificate of ExampleStack. Recently, Chrome Web Browser requires that a server must be protected by SSL if a Chrome extension wants to connect to the server.

5. ExampleStack Manual.pdf includes the instructions of installing the Chrome extension and setting up the ExampleStack server, as well as a step-by-step demonstration of the tool features in ExampleStack.

6. examplestack-server.jar is the jar file of the ExampleStack server.

Please follow instructions in "ExampleStack Manual.pdf" to install and explore ExampleStack. 

We want to clarify two questions asked by other users when trying our tool, just in case that you have similar issues. 
First, please make sure that you follow the instructions to make your browser trust the SSL certificate of ExampleStack. The steps are elaborated in the tool manual.
Second, the Chrome extension connects to the server via web socket. Therefore, if the web socket stays idle for a while, it will be closed. So if you find the Chrome extension does not respond, simply reload the webpage and the websocket will be re-connected.
