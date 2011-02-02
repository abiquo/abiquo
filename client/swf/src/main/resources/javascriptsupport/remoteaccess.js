///////////////////////////////////////////////////////
//Javascript code to load Java applets for Remote Access

	//Called from Flex Application to use an iFrame to load a Java Applet
	function loadRemoteAccessJavaClient(remoteURL)
	{
		//Setting URL to iFrame
		var raIframe = document.getElementById("raIframe");
		raIframe.src = remoteURL;

		//Making it visible
		var raDiv = document.getElementById("raDiv");
		raDiv.style.display = "block";
	}
	
	//Hides and unloads the iframe used to load the Java Applet
	function unloadRemoteAccessJavaClient()
	{
		var raDiv = document.getElementById("raDiv");
		raDiv.style.display = "none";
		
		var raIframe = document.getElementById("raIframe");
		raIframe.src = "";
	}