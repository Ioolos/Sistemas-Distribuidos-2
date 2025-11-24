// Mostrar cuenta activa
const cuenta = localStorage.getItem("cuentaActiva");
if (!cuenta) {
  alert("Sesión no iniciada.");
  window.location.href = "index.html";
} else {
  document.getElementById("cuentaActiva").textContent = "Cuenta: " + cuenta;
}

// Cerrar sesión
function cerrarSesion() {
  localStorage.removeItem("cuentaActiva");
  window.location.href = "index.html";
}

// Ventanas modales
const modal = document.getElementById("modal");
const contenidoModal = document.getElementById("contenidoModal");
const modalMensaje = document.getElementById("modalMensaje");
const mensaje = document.getElementById("mensaje");

// Abrir modal dinámico según operación
function abrirModal(tipo) {
  let html = "";

  if (tipo === "saldo") {
    consultarSaldo();
    return;
  }

  if (tipo === "deposito") {
    html = `
      <h3>Depósito</h3>
      <input type="number" id="montoDeposito" placeholder="Monto a depositar">
      <button onclick="realizarOperacion('deposito')">Confirmar</button>
    `;
  } else if (tipo === "retiro") {
    html = `
      <h3>Retiro</h3>
      <input type="number" id="montoRetiro" placeholder="Monto a retirar">
      <button onclick="realizarOperacion('retiro')">Confirmar</button>
    `;
  } else if (tipo === "transferencia") {
    html = `
      <h3>Transferencia</h3>
      <input type="text" id="cuentaDestino" placeholder="Cuenta destino">
      <input type="number" id="montoTransferencia" placeholder="Monto a transferir">
      <button onclick="realizarOperacion('transferencia')">Confirmar</button>
    `;
  }

  contenidoModal.innerHTML = html;
  modal.style.display = "flex";
}

// Cerrar modal
function cerrarModal() {
  modal.style.display = "none";
}

// Mostrar mensaje modal
function mostrarMensaje(texto, color = "green") {
  mensaje.style.color = color;
  mensaje.textContent = texto;
  modalMensaje.style.display = "flex";

  setTimeout(() => {
    cerrarModal();
    cerrarMensaje();
  }, 3000);
}

// Cerrar mensaje modal
function cerrarMensaje() {
  modalMensaje.style.display = "none";
}

// Función común para operaciones POST
function realizarOperacion(tipo) {
  let body = {};
  let url = "";
  if (tipo === "deposito") {
    const monto = parseFloat(document.getElementById("montoDeposito").value);
    if (isNaN(monto) || monto <= 0) return mostrarMensaje("Monto inválido", "red");
    body = { cuenta, monto };
    url = "http://localhost:8083/deposito";
  }

  if (tipo === "retiro") {
    const monto = parseFloat(document.getElementById("montoRetiro").value);
    if (isNaN(monto) || monto <= 0) return mostrarMensaje("Monto inválido", "red");
    body = { cuenta, monto };
    url = "http://localhost:8083/retiro";
  }

  if (tipo === "transferencia") {
    const destino = document.getElementById("cuentaDestino").value;
    const monto = parseFloat(document.getElementById("montoTransferencia").value);
    if (!destino || destino === cuenta) return mostrarMensaje("Cuenta destino inválida", "red");
    if (isNaN(monto) || monto <= 0) return mostrarMensaje("Monto inválido", "red");
    body = {
      cuentaOrigen: cuenta,
      cuentaDestino: destino,
      monto
    };
    url = "http://localhost:8083/transferencia";
  }

  fetch(url, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body)
  })
    .then(res => res.text())
    .then(mensajeServidor => mostrarMensaje(mensajeServidor, "green"))
    .catch(() => mostrarMensaje("Error en la operación", "red"));
}

// Saldo con ventana modal
function consultarSaldo() {
  fetch(`http://localhost:8082/saldo?cuenta=${cuenta}`)
    .then(res => res.text())
    .then(saldo => {
      contenidoModal.innerHTML = `
        <h3>Saldo disponible</h3>
        <p style="font-size: 24px; text-align: center;"><strong>$${saldo}</strong></p>
      `;
      modal.style.display = "flex";
    })
    .catch(() => mostrarMensaje("Error al consultar saldo", "red"));
}
