// Modal functions
function openAddMethodModal() {
	const userId = document.body.dataset.userId;
	const fromPage = document.body.dataset.fromPage

	fetch("/methods/add/" + userId + "?fromPage=" + encodeURIComponent(fromPage))
	    .then(response => response.text())
	    .then(html => {
	      document.getElementById("modalBody").innerHTML = html;
	      document.getElementById("modal").style.display = "flex";
	    });
}

function openAddTransactionModal() {
	const userId = document.body.dataset.userId;
	const fromPage = document.body.dataset.fromPage
	
	fetch("/transactions/add/" + userId + "?fromPage=" + encodeURIComponent(fromPage))
	    .then(response => response.text())
	    .then(html => {
	      document.getElementById("modalBody").innerHTML = html;
	      document.getElementById("modal").style.display = "flex";
	    });
}

function openGenerateReportModal() {
	
  const userId = document.body.dataset.userId;
  const fromPage = document.body.dataset.fromPage;		

  fetch('/reports/add/' + userId + "?fromPage=" + encodeURIComponent(fromPage)) 
    .then(response => response.text())
    .then(html => {
      document.getElementById("modalBody").innerHTML = html;
      
      initializeDateFields();
      
      document.getElementById('modal').style.display = 'flex';
    })
    .catch(error => console.error('Errore nel caricamento del form:', error));
}



function closeModal() {
  document.getElementById("modal").style.display = "none";
}

// Chiudi la modal cliccando fuori
window.onclick = function(event) {
  const modal = document.getElementById("modal");
  if (event.target == modal) {
    modal.style.display = "none";
  }
}

function filterCategories() {
    const typeSelect = document.getElementById("type");
    const categorySelect = document.getElementById("category");

	
	console.log(typeSelect)
	console.log(categorySelect)
    if (!typeSelect || !categorySelect) return;

    const selectedType = typeSelect.value;
	console.log(selectedType)

    // Nasconde tutte le opzioni tranne quella iniziale
    Array.from(categorySelect.options).forEach((option, index) => {
        if (option.value === "") {
            option.style.display = ""; // Prima opzione visibile sempre
        } else {
            const optionType = option.getAttribute("data-type");
            option.style.display = optionType === selectedType ? "" : "none";
        }
    });

    categorySelect.value = ""; // reset
}

document.addEventListener("DOMContentLoaded", function () {
  const showTransactionModal = /*[[${showAddTransactionModal}]]*/ false;
  const showMethodModal = /*[[${showAddTransactionModal}]]*/ false;
  if (showTransactionModal) {
    // Chiama la tua funzione JS per aprire la modal
    openAddTransactionModal();
  }
  if(showMethodModal) {
	openAddMethodModal();
  }
});

let formToSubmit = null;
function confirmDeleteMethod(button) {
  const form = button.closest("form");
  const transactionCount = parseInt(form.dataset.transactionsCount);
  const methodName = form.dataset.methodName;

  if (transactionCount > 0) {
    const modal = document.getElementById("modal");
    const body = document.getElementById("modalBody");

    formToSubmit = form;

    // Testo di allerta
    body.innerHTML = `
      <p>⚠️ <strong>Attenzione</strong>: sono associate ${transactionCount} transazioni al metodo "<strong>${methodName}</strong>".<br>Sei sicuro di voler procedere?</p>
      <div class="modal-actions">
        <button class="btn btn-confirm" onclick="proceedDelete()">✅ Procedi</button>
        <button class="btn btn-cancel" onclick="closeModal()">❌ Annulla</button>
      </div>
    `;

    modal.style.display = "flex";
  } else {
    form.submit();
  }
}

function proceedDelete() {
  if (formToSubmit) {
    formToSubmit.submit();
  }
}


//Notifiche

// Mostra la notifica con animazione
if(document.getElementById('notification') != null) {
	setTimeout(() => {
	    document.getElementById('notification').classList.add('show');
	}, 50);
}

// Auto-chiudi dopo 5 secondi
if(document.getElementById('notification') != null) {
	setTimeout(() => {
	    closeNotification();
	}, 3000);
}
	
function closeNotification() {
    const notification = document.getElementById('notification');
    if (notification) {
        notification.classList.remove('show');
        notification.classList.add('hide');
        
        // Pulisci URL dopo l'animazione
        setTimeout(() => {
            if (window.history.replaceState) {
                const url = new URL(window.location);
                url.searchParams.delete('change');
                window.history.replaceState({}, document.title, url.toString());
            }
        }, 500);
    }
}


// Auto Selezione date in Genera Report

function initializeDateFields() {
  const endDateInput = document.getElementById("end");
  const startDateInput = document.getElementById("start");

  if (endDateInput && startDateInput) {
    const today = new Date();
    const startDate = new Date();
    startDate.setDate(today.getDate() - 30);

    const formatDate = (date) => date.toISOString().split('T')[0];

    endDateInput.value = formatDate(today);
    startDateInput.value = formatDate(startDate);
  }
}


function toggleAllCategories(masterCheckbox) {
  const categoryCheckboxes = document.querySelectorAll(".category-checkbox");
  categoryCheckboxes.forEach(cb => {
    cb.checked = masterCheckbox.checked;
  });
}
