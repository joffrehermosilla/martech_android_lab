# AJO Lab Martech Engineer BCP CRM - Auditoría Técnica

Manual de Ingeniería para la validación y auditoría del ecosistema **AEP + Real-Time CDP**.

---

## 🏗️ Sincronización de Identidad (Namespace CRMID)

Para que el **Identity Stitching** funcione, el código debe coincidir exactamente con el "Identity Symbol" configurado en Adobe Platform.

- **ECID (Dispositivo):** Generado automáticamente por el SDK.
- **CustomerID (Persona):** Valor `jo89`.
- **Namespace Técnico:** Se ha detectado que el símbolo correcto en este proyecto es `CRMID`.
  - *Nota:* Si se usa un namespace inexistente en Adobe (como "CRM"), CDP no podrá procesar el perfil.

---

## ⚠️ Checklist de Verificación en CDP

Si al buscar el perfil de Joffre no ves eventos, verifica estos 3 puntos:

1.  **Identity Symbol:** Confirma en *Identities > Browse* que el código para "Customer ID" es exactamente `CRMID`.
2.  **Edge Network:** Asegúrate de que la extensión en Launch esté configurada y **Publicada** en el entorno de Development.
3.  **Active ID:** En el Dashboard de la app, pulsa el botón **Identity** para asegurar que el ID de Joffre esté activo en la sesión actual.

---

## 📲 Notificaciones y Push Token

Para realizar pruebas de envío desde AJO:

- **Token de Prueba:** `dlSZkjssbvndD0EAC_WRL0x8mtdPgtAe1c-IQac`
- **App ID:** `com.adobeingsample`
- **Channel ID:** `bcp_push_channel`
- **Audio:** `sonomarca` (Archivo `res/raw/sonomarca.mp3`).

---

## 🔍 Estructura del Evento Corporativo (_bcp)

```json
{
  "_bcp": {
    "identity": { "customerId": "jof89" },
    "event": { "name": "moion" },
    "mobile": {
      "action": {
        "group": "LocationServices",
        "category": "AdobePlaces.POI",
        "label": "POI Entry: BCP San Isidro"
      }
    }
  }
}
```
