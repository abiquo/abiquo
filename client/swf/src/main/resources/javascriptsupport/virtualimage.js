////////////////////////////////////////////////
//Javascript code to support VirtualImage creation


	var viFormSubmited = false;
	
	//Flex application relies on Web browser to download VirtualImage files
	function downloadVirtualImageFile(fileURL)
	{
		var viDownloadIframe = document.getElementById("viDownloadIframe");
		viDownloadIframe.src = fileURL;
	}
	
	//Shows the VirtualImage Form used to create the VirtualImages
	function showVirtualImageForm(resetForm)
	{
		if(resetForm)
		{
			var viForm = document.getElementById("viForm");
			viForm.reset();
		}
		
		//Making the viForm visible
		var viFormDiv = document.getElementById("viFormDiv");
		viFormDiv.style.display = "block";
	}
	
	//Hides the VirtualImage form
	function hideVirtualImageForm()
	{
		//Making the viForm invisible
		var viFormDiv = document.getElementById("viFormDiv");
		viFormDiv.style.display = "none";
	}
	
	//Submits the VirtualImage form
	function submitVirtualImageForm(viInfo, submitAddress)
	{		
		//Hidding the VirtualImage Form
		hideVirtualImageForm();
		
		//Setting the DiskInfo
		var viDiskInfo = document.getElementById("viDiskInfo");
		viDiskInfo.value = viInfo;
		
		//Setting the form action and submit it
		var viForm = document.getElementById("viForm");
		viForm.action = submitAddress;
		viForm.submit();
		
		viFormSubmited = true;
	}
	
	//Function called when user selects a file from its local system
	function onChangeDiskFile()
	{
		var viDiskFile = document.getElementById("viDiskFile");
		if(viDiskFile.value != "")
		{
			//Notifying that user has selected a File to upload
			flexApplication.virtualImageFileSelected(viDiskFile.value);
		}
	}
	
	//Function called when we got a response from server where the VirtualImage from was submited
	function onVirtualImageFormSubmitComplete()
	{
		//TODO: Check if the call ended successfully

		if(viFormSubmited)
		{
			//Cleaning things
			var viForm = document.getElementById("viForm");
			viForm.reset();
			viForm.action = "";
			
			var viFormIframe = document.getElementById("viFormIframe");
			viFormIframe.src = "";
			
			viFormSubmited = false;
			
			flexApplication.virtualImageCreated();
		}
	}