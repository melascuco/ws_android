<?php
	//Function to send PUSH
   function sendPush($registatoin_ids, $message) {
		//Google cloud messaging GCM-API url
        $url = 'https://android.googleapis.com/gcm/send';
        $fields = array(
            'registration_ids' => $registatoin_ids,
            'data' => $message,
        );
		// Google Cloud Messaging GCM API Key
		define("GOOGLE_API_KEY", ""); 	// GCM server key
        $headers = array(
            'Authorization: key=' . GOOGLE_API_KEY,
            'Content-Type: application/json'
        );
        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, $url);
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
		curl_setopt ($ch, CURLOPT_SSL_VERIFYHOST, 0);	
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
        $result = curl_exec($ch);
		var_dump($result);
		if ($result === FALSE) {
            die('Curl failed: ' . curl_error($ch));
        //curl_close($ch);
        }
		
        return $result;
    }
	
	//Send Push Notification
	$pushStatus = "";	
	if(!empty($_GET["push"])) {	
		$gcmRegID  = file_get_contents("GCMRegId.txt");
		$pushMessage = $_POST["message"];	
		if (isset($gcmRegID) && isset($pushMessage)) {		
			$gcmRegIds = array($gcmRegID);
			$message = array("msg" => $pushMessage);	
			$pushStatus = sendPush($gcmRegIds, $message);
		}		
	}
	
	// Receive GCM ID
	if(!empty($_GET["shareRegId"])) {
		$gcmRegID  = $_POST["regId"]; 
		file_put_contents("GCMRegId.txt",$gcmRegID);
		echo "Ok!";
		exit;
	}	
?>
<html>
    <head>
        <title>Push Notification android using GCM</title>
    </head>
	<body>
		<h1>Push Notification using GCM</h1>	
		<form method="post" action="?push=1">					                             
			<div>                                
				<textarea rows="2" name="message" cols="23" placeholder="Message to transmit via GCM"></textarea>
			</div>
			<div><input type="submit"  value="Send Push Notification via GCM" /></div>
		</form>
		<p><h3><?php echo $pushStatus; ?></h3></p>        
    </body>
</html>