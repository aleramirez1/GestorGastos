const functions = require("firebase-functions/v1");
const admin = require("firebase-admin");
const nodemailer = require("nodemailer");

admin.initializeApp();

exports.notifications = functions
  .database.ref("/invitaciones/{invitacionId}")
  .onCreate(async (snapshot, context) => {
    const invitacion = snapshot.val();

    if (!invitacion) return null;

    const {
      invitado_email,
      grupo_nombre,
      invitado_por,
      codigo_invitacion,
      tipo,
    } = invitacion;

    if (tipo !== "invitacion_email" || !invitado_email) return null;

    const gmailUser = process.env.GMAIL_USER;
    const gmailPass = process.env.GMAIL_PASS;

    const transporter = nodemailer.createTransport({
      host: "smtp.gmail.com",
      port: 587,
      secure: false,
      auth: {
        user: gmailUser,
        pass: gmailPass,
      },
    });

    const mailOptions = {
      from: `"Gestor de Gastos" <${gmailUser}>`,
      to: invitado_email,
      subject: `¡Te invitaron al grupo "${grupo_nombre}"!`,
      html: `
        <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f4f4f4;">
          <div style="background-color: white; border-radius: 10px; padding: 30px;">
            <h1 style="color: #5C6BC0; text-align: center;">¡Te invitaron a un grupo!</h1>
            <p><strong>${invitado_por}</strong> te invita al grupo <strong style="color: #4FC3F7;">${grupo_nombre}</strong> en Gestor de Gastos.</p>
            <div style="background-color: #E3F2FD; padding: 20px; border-radius: 8px; margin: 20px 0; text-align: center;">
              <p style="margin: 0; font-size: 14px; color: #5C6BC0;"><strong>Tu código de invitación:</strong></p>
              <p style="margin: 10px 0; font-size: 28px; color: #5C6BC0; font-weight: bold; letter-spacing: 4px;">${codigo_invitacion}</p>
            </div>
            <ol>
              <li>Descarga la app <strong>Gestor de Gastos</strong></li>
              <li>Toca <strong>"Unirse a Grupo"</strong> en la pantalla de inicio</li>
              <li>Ingresa el código de arriba</li>
              <li>Regístrate y el grupo aparecerá automáticamente</li>
            </ol>
          </div>
        </div>
      `,
    };

    try {
      await transporter.sendMail(mailOptions);
      console.log(`Email enviado a ${invitado_email}`);
      await snapshot.ref.update({ estado: "enviado" });
    } catch (error) {
      console.error("Error al enviar email:", error);
      await snapshot.ref.update({ estado: "error", error_msg: error.message });
    }

    return null;
  });
