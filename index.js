const functions = require('firebase-functions');
const admin = require('firebase-admin');
const nodemailer = require('nodemailer');

admin.initializeApp();

const transporter = nodemailer.createTransport({
  service: 'gmail',
  auth: {
    user: '233380@ids.upchiapas.edu.mx',
    pass: 'ejpetovxlgrpqmpn'
  }
});

exports.enviarInvitacionCompleta = functions.database
  .ref('/invitaciones/{invitacionId}')
  .onCreate(async (snapshot, context) => {
    const invitacion = snapshot.val();
    
    console.log('Nueva invitacion detectada:', invitacion.invitado_email);
    
    const notificationPayload = {
      notification: {
        title: `Invitacion al grupo "${invitacion.grupo_nombre}"`,
        body: `${invitacion.invitado_por} te invita a unirte`,
        icon: 'ic_notification',
        sound: 'default',
        clickAction: 'FLUTTER_NOTIFICATION_CLICK'
      },
      data: {
        tipo: 'invitacion_grupo',
        grupo_id: String(invitacion.grupo_id),
        grupo_nombre: invitacion.grupo_nombre,
        invitado_por: invitacion.invitado_por,
        invitado_email: invitacion.invitado_email,
        timestamp: String(Date.now())
      }
    };
    
    try {
      const fcmResponse = await admin.messaging().sendToTopic('invitaciones', notificationPayload);
      console.log('Push notification enviada exitosamente:', fcmResponse.messageId);
      
      await snapshot.ref.update({ 
        fcm_enviado: true,
        fcm_timestamp: Date.now(),
        fcm_message_id: fcmResponse.messageId
      });
    } catch (fcmError) {
      console.error('Error al enviar push notification:', fcmError.message);
      
      await snapshot.ref.update({ 
        fcm_enviado: false, 
        fcm_error: fcmError.message
      });
    }
    
    const mailOptions = {
      from: 'Gestor de Gastos <233380@ids.upchiapas.edu.mx>',
      to: invitacion.invitado_email,
      subject: `Invitacion al grupo "${invitacion.grupo_nombre}"`,
      html: `
        <div style="font-family:Arial;max-width:600px;margin:20px auto;background:white;border-radius:10px;padding:30px;box-shadow:0 2px 10px rgba(0,0,0,0.1)">
          <div style="background:#5C6BC0;padding:30px;text-align:center;border-radius:10px 10px 0 0">
            <h1 style="color:white;margin:0;font-size:24px">Invitacion a Grupo</h1>
          </div>
          <div style="padding:30px">
            <p style="font-size:16px;color:#333">Hola,</p>
            <p style="font-size:16px;color:#333">
              <strong>${invitacion.invitado_por}</strong> te ha invitado al grupo 
              <strong style="color:#4FC3F7">"${invitacion.grupo_nombre}"</strong> en Gestor de Gastos.
            </p>
            <div style="background:#E3F2FD;padding:20px;border-radius:8px;margin:20px 0">
              <p style="margin:0;font-size:14px;color:#5C6BC0">
                <strong>ID del Grupo:</strong> ${invitacion.grupo_id}
              </p>
              <p style="margin:10px 0 0 0;font-size:14px;color:#666">
                ${invitacion.mensaje}
              </p>
            </div>
            <p style="font-size:16px;color:#333">
              Abre la app <strong>Gestor de Gastos</strong> en tu celular para aceptar la invitacion.
            </p>
            <div style="text-align:center;margin-top:30px;padding-top:20px;border-top:1px solid #eee">
              <p style="font-size:12px;color:#999;margin:0">
                Este correo fue enviado automaticamente por Gestor de Gastos.
              </p>
            </div>
          </div>
        </div>
      `
    };
    
    try {
      const info = await transporter.sendMail(mailOptions);
      console.log('Email enviado exitosamente:', info.messageId);
      console.log('Destinatario:', invitacion.invitado_email);
      
      await snapshot.ref.update({ 
        email_enviado: true,
        email_timestamp: Date.now(),
        email_message_id: info.messageId
      });
      
      return null;
    } catch (error) {
      console.error('Error al enviar email:', error.message);
      
      await snapshot.ref.update({ 
        email_enviado: false, 
        email_error: error.message,
        email_error_timestamp: Date.now()
      });
      
      return null;
    }
  });
