const functions = require('firebase-functions');
const admin = require('firebase-admin');
const nodemailer = require('nodemailer');

admin.initializeApp();

const transporter = nodemailer.createTransport({
  service: 'gmail',
  auth: {
    user: '233380@ids.upchiapas.edu.mx',
    pass: 'abcdefghijklmnop'
  }
});

exports.enviarEmailInvitacion = functions.database
  .ref('/invitaciones/{invitacionId}')
  .onCreate(async (snapshot, context) => {
    const invitacion = snapshot.val();
    
    console.log('Nueva invitación:', invitacion.invitado_email);
    
    const mailOptions = {
      from: 'Gestor de Gastos <233380@ids.upchiapas.edu.mx>',
      to: invitacion.invitado_email,
      subject: `Invitación al grupo "${invitacion.grupo_nombre}"`,
      html: `
        <!DOCTYPE html>
        <html>
        <body style="margin:0;padding:0;font-family:Arial,sans-serif;background-color:#f4f4f4">
          <div style="max-width:600px;margin:20px auto;background-color:white;border-radius:10px;overflow:hidden;box-shadow:0 2px 10px rgba(0,0,0,0.1)">
            <div style="background-color:#5C6BC0;padding:30px;text-align:center">
              <h1 style="color:white;margin:0;font-size:24px">Invitación a Grupo</h1>
            </div>
            <div style="padding:30px">
              <p style="font-size:16px;color:#333;margin-bottom:20px">Hola,</p>
              <p style="font-size:16px;color:#333;margin-bottom:20px">
                <strong>${invitacion.invitado_por}</strong> te ha invitado al grupo 
                <strong style="color:#4FC3F7">"${invitacion.grupo_nombre}"</strong> en Gestor de Gastos.
              </p>
              <div style="background-color:#E3F2FD;padding:20px;border-radius:8px;margin:20px 0">
                <p style="margin:0;font-size:14px;color:#5C6BC0">
                  <strong>📋 ID del Grupo:</strong> ${invitacion.grupo_id}
                </p>
                <p style="margin:10px 0 0 0;font-size:14px;color:#666">
                  ${invitacion.mensaje}
                </p>
              </div>
              <p style="font-size:16px;color:#333;margin-bottom:20px">
                Abre la app <strong>Gestor de Gastos</strong> para aceptar la invitación.
              </p>
              <div style="text-align:center;margin-top:30px;padding-top:20px;border-top:1px solid #eee">
                <p style="font-size:12px;color:#999;margin:0">
                  Este correo fue enviado automáticamente.
                </p>
              </div>
            </div>
          </div>
        </body>
        </html>
      `
    };
    
    try {
      const info = await transporter.sendMail(mailOptions);
      console.log(' Email enviado:', info.messageId);
      
      await snapshot.ref.update({ 
        email_enviado: true,
        email_timestamp: Date.now()
      });
      
      return null;
    } catch (error) {
      console.error(' Error:', error.message);
      
      await snapshot.ref.update({ 
        email_enviado: false, 
        email_error: error.message
      });
      
      return null;
    }
  });
