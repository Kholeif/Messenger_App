const functions = require('firebase-functions');

const admin = require('firebase-admin');
 admin.initializeApp();

 exports.notifyNewMessage = functions.firestore
    .document('chat_channels/{channel}/messages/{message}')
     .onCreate((docSnapshot, context) => {
        const message = docSnapshot.data();
        const recieptientID = message['recieptientID'];
        const senderName = message['senderName'];
		const senderId = message['senderID'];
		const path = message['sender_image_path'];

        return admin.firestore().doc('users/' + recieptientID).get().then(userDoc => {
            const registrationTokens = userDoc.get('token')
			
         
            const notificationBody = (message['type'] === "TEXT") ? message['text'] : "You received a new image message."
            const payload = {
                data: {
                 title: senderName + " sent you a message.",
                 USER_NAME: senderName ,
			     body: notificationBody ,
				 sender: senderId ,
				 image_path: path
			   }
         }
            return admin.messaging().sendToDevice(registrationTokens, payload).then( response => {
                const stillRegisteredTokens = registrationTokens
 
                response.results.forEach((result, index) => {
                    const error = result.error
                    if (error) {
                        const failedRegistrationToken = registrationTokens[index]
                        console.error('blah', failedRegistrationToken, error)
                        if (error.code === 'messaging/invalid-registration-token'
                            || error.code === 'messaging/registration-token-not-registered') {
                                const failedIndex = stillRegisteredTokens.indexOf(failedRegistrationToken)
                                if (failedIndex > -1) {
                                    stillRegisteredTokens.splice(failedIndex, 1)
                                }
                            }
                    }
                })
          return admin.firestore().doc("users/" + recieptientID).update({
                    registrationTokens: stillRegisteredTokens
                })
            })
        })
    })