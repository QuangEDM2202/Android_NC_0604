 // 'use strict' 
 // const functions = require('firebase-functions'); 
 // const admin = require('firebase-admin'); 
 // admin.initializeApp(functions.config().firebase); 
 // exports.sendNotification = functions.database.ref('/notifications/{user_id}/{notification_id}').onWrite((data, context) => { 
 // 	const user_id = context.params.user_id; 
 // 	const notification_id = context.params.notification_id; 
 // 	console.log('We have a notification to send to  : ', user_id); 

 // 	 if(!context.data.val()){
 // 	 	return console('Mot thong bao bi xoa khoi database:',notification_id);
 // 	 }

 // 	const fromUser = admin.database().ref(`/notification/${user_id}/${notification_id}`).once('value');
 // 	return fromUser.then(fromUserResult =>{
 // 		// lay gia tri from trong truong notification
 // 		const from_user_id = fromUserResult.val().from;
 // 		console('You have a new notification from :',from_user_id);
 // 		const deviceToken = admin.database().ref(`/Users/${user_id}/device_token`).once('value');
 // 		return deviceToken.then(result=>{
 // 			const token_id=result.val();
 // 			const payload={
 // 				notification:{
 // 					title: "FRiend Request",
 // 					body: "You have received a friend request",
 // 					icon:"default"}
 // 				};    
 // 				return admin.messaging().sendToDevice(token_id,payload).then(response =>{
 // 					console.log('This was notification feature');
 // 					return true;
 // 				});

 // 			}); 
 // 	});
 // });
/*
 * Functions SDK : is required to work with firebase functions.
 * Admin SDK : is required to send Notification using functions.
 */

'use strict'

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);


/*
 * 'OnWrite' works as 'addValueEventListener' for android. It will fire the function
 * everytime there is some item added, removed or changed from the provided 'database.ref'
 * 'sendNotification' is the name of the function, which can be changed according to
 * your requirement
 */

exports.sendNotification = functions.database.ref('/notifications/{user_id}/{notification_id}').onWrite((change,context) => {


  /*
   * You can store values as variables from the 'database.ref'
   * Just like here, I've done for 'user_id' and 'notification'
   */


  const user_id = context.params.user_id;
  const notification_id = context.params.notification_id;

  console.log('We have a notification to : ', user_id);

  /*
   * Stops proceeding to the rest of the function if the entry is deleted from database.
   * If you want to work with what should happen when an entry is deleted, you can replace the
   * line from "return console.log.... "
   */
 console.log('context.data before truoc: ', change.before.val());
 console.log('context.data after sau : ', change.after.val());
  if(!change.after.val()){

    return console.log('A Notification has been deleted from the database : ', notification_id);

  }

  /*
   * 'fromUser' query retreives the ID of the user who sent the notification
   */

  const fromUser = admin.database().ref(`/notifications/${user_id}/${notification_id}`).once('value');

  return fromUser.then(fromUserResult => {

    const from_user_id = fromUserResult.val().from;

    console.log('You have new notification from  : ', from_user_id);

    /*
     * The we run two queries at a time using Firebase 'Promise'.
     * One to get the name of the user who sent the notification
     * another one to get the devicetoken to the device we want to send notification to
     */

    const userQuery = admin.database().ref(`Users/${from_user_id}/name`).once('value');
    const deviceToken = admin.database().ref(`/Users/${user_id}/device_token`).once('value');

    return Promise.all([userQuery, deviceToken]).then(result => {

      const userName = result[0].val();
      const token_id = result[1].val();

      /*
       * We are creating a 'payload' to create a notification to be sent.
       */

      const payload = {
        notification: {
          title : "New Friend Request",
          body: `${userName} has sent you request`,
          icon: "default",
          click_action : "com.quangedm2202.fithou_chat_TARGET_NOTIFICATION"
        },
        data : {
          from_user_id : from_user_id
        }
      };

      /*
       * Then using admin.messaging() we are sending the payload notification to the token_id of
       * the device we retreived.
       */

      return admin.messaging().sendToDevice(token_id, payload)
      });

    });

  });





// exports.sendNotification = functions.database.ref('/notifications/{user_id}/{notification_id}').onWrite((change,context) => {


//   /*
//    * You can store values as variables from the 'database.ref'
//    * Just like here, I've done for 'user_id' and 'notification'
//    */


//   const user_id = context.params.user_id;
//   const notification_id = context.params.notification_id;

//   console.log('We have a notification from : ', user_id);

//   /*
//    * Stops proceeding to the rest of the function if the entry is deleted from database.
//    * If you want to work with what should happen when an entry is deleted, you can replace the
//    * line from "return console.log.... "
//    */
//  console.log('context.data before truoc: ', change.before.val());
//  console.log('context.data after sau : ', change.after.val());
//   if(!change.after.val()){

//     return console.log('A Notification has been deleted from the database : ', notification_id);

//   }

//   /*
//    * 'fromUser' query retreives the ID of the user who sent the notification
//    */

//   const fromUser = admin.database().ref(`/notifications/${user_id}/${notification_id}`).once('value');

//   return fromUser.then(fromUserResult => {

//     const from_user_id = fromUserResult.val().from;

//     console.log('You have new notification from  : ', from_user_id);

    
//      * The we run two queries at a time using Firebase 'Promise'.
//      * One to get the name of the user who sent the notification
//      * another one to get the devicetoken to the device we want to send notification to
     

//     const userQuery = admin.database().ref(`Users/${from_user_id}/name`).once('value');
//     const deviceToken = admin.database().ref(`/Users/${user_id}/device_token`).once('value');

//     return Promise.all([userQuery, deviceToken]).then(result => {

//       const userName = result[0].val();
//       const token_id = result[1].val();

//       /*
//        * We are creating a 'payload' to create a notification to be sent.
//        */

//       const payload = {
//         notification: {
//           title : "New Friend Request",
//           body: `${userName} has sent you request`,
//           icon: "default",
//           click_action : "in.tvac.akshaye.lapitchat_TARGET_NOTIFICATION"
//         },
//         data : {
//           from_user_id : from_user_id
//         }
//       };

//       /*
//        * Then using admin.messaging() we are sending the payload notification to the token_id of
//        * the device we retreived.
//        */

//       return admin.messaging().sendToDevice(token_id, payload).then(response => {

//         console.log('This was the notification Feature');
//         return true;

//       });

//     });

//   });

// });

