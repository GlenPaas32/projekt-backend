const state = {
    disciplines: [],
    athlete: null,
    toastTimer: null
};

const els = {
    apiStatus: document.querySelector("#apiStatus"),
    toast: document.querySelector("#toast"),
    disciplineForm: document.querySelector("#disciplineForm"),
    disciplineRows: document.querySelector("#disciplineRows"),
    refreshDisciplines: document.querySelector("#refreshDisciplines"),
    athleteForm: document.querySelector("#athleteForm"),
    lookupForm: document.querySelector("#lookupForm"),
    athleteSummary: document.querySelector("#athleteSummary"),
    resultForm: document.querySelector("#resultForm"),
    disciplineSelect: document.querySelector("#resultForm select[name='discipline']"),
    resultsList: document.querySelector("#resultsList"),
    loadPoints: document.querySelector("#loadPoints")
};

async function request(path, options = {}) {
    const response = await fetch(path, {
        headers: { "Content-Type": "application/json", ...(options.headers || {}) },
        ...options
    });

    if (!response.ok) {
        let message = `Viga ${response.status}`;
        try {
            const body = await response.json();
            message = body.message || body.error || body.detail || message;
        } catch {
            const text = await response.text();
            message = text || message;
        }
        throw new Error(message);
    }

    if (response.status === 204) {
        return null;
    }
    return response.json();
}

function showToast(message) {
    window.clearTimeout(state.toastTimer);
    els.toast.textContent = message;
    els.toast.classList.add("show");
    state.toastTimer = window.setTimeout(() => {
        els.toast.classList.remove("show");
    }, 3200);
}

function setApiStatus(ok) {
    els.apiStatus.textContent = ok ? "Backend OK" : "Backend viga";
    els.apiStatus.classList.toggle("error", !ok);
}

function setLoading(form, isLoading) {
    form.querySelectorAll("button, input, select").forEach((element) => {
        element.disabled = isLoading;
    });
}

function renderDisciplines() {
    if (!state.disciplines.length) {
        els.disciplineRows.innerHTML = "<tr><td colspan=\"3\">Alasid pole</td></tr>";
        els.disciplineSelect.innerHTML = "<option value=\"\">Vali ala</option>";
        return;
    }

    els.disciplineRows.innerHTML = state.disciplines
        .map((discipline) => `
            <tr>
                <td>${discipline.id ?? ""}</td>
                <td>${escapeHtml(discipline.name)}</td>
                <td>${Number(discipline.pointsFactor).toFixed(2)}</td>
            </tr>
        `)
        .join("");

    els.disciplineSelect.innerHTML = "<option value=\"\">Vali ala</option>" + state.disciplines
        .map((discipline) => `<option value="${escapeAttribute(discipline.name)}">${escapeHtml(discipline.name)}</option>`)
        .join("");
}

function renderAthlete() {
    const athlete = state.athlete;
    if (!athlete) {
        els.athleteSummary.innerHTML = "<span>Vali või loo sportlane</span>";
        els.resultsList.innerHTML = "";
        return;
    }

    els.lookupForm.elements.athleteId.value = athlete.id;
    els.athleteSummary.innerHTML = `
        <div class="metric-row"><span>ID</span><strong>${athlete.id}</strong></div>
        <div class="metric-row"><span>Nimi</span><strong>${escapeHtml(athlete.name)}</strong></div>
        <div class="metric-row"><span>Tulemusi</span><strong>${athlete.results?.length ?? 0}</strong></div>
    `;

    renderResults(athlete.results || []);
}

function renderResults(results) {
    if (!results.length) {
        els.resultsList.innerHTML = "<div class=\"athlete-summary\"><span>Tulemusi pole</span></div>";
        return;
    }

    els.resultsList.innerHTML = results
        .map((result) => `
            <article class="result-item">
                <div>
                    <strong>${escapeHtml(result.discipline)}</strong>
                    <span>${Number(result.resultValue).toFixed(2)}</span>
                </div>
                <div class="result-points">${result.points ?? 0} p</div>
            </article>
        `)
        .join("");
}

async function loadDisciplines() {
    try {
        state.disciplines = await request("/api/disciplines");
        renderDisciplines();
        setApiStatus(true);
    } catch (error) {
        setApiStatus(false);
        showToast(error.message);
    }
}

async function createDiscipline(event) {
    event.preventDefault();
    const formData = new FormData(els.disciplineForm);
    setLoading(els.disciplineForm, true);

    try {
        await request("/api/disciplines", {
            method: "POST",
            body: JSON.stringify({
                name: formData.get("name").trim(),
                pointsFactor: Number(formData.get("pointsFactor"))
            })
        });
        els.disciplineForm.reset();
        await loadDisciplines();
        showToast("Ala lisatud");
    } catch (error) {
        showToast(error.message);
    } finally {
        setLoading(els.disciplineForm, false);
    }
}

async function createAthlete(event) {
    event.preventDefault();
    const formData = new FormData(els.athleteForm);
    setLoading(els.athleteForm, true);

    try {
        state.athlete = await request("/athletes", {
            method: "POST",
            body: JSON.stringify({ name: formData.get("name").trim() })
        });
        els.athleteForm.reset();
        renderAthlete();
        showToast("Sportlane loodud");
    } catch (error) {
        showToast(error.message);
    } finally {
        setLoading(els.athleteForm, false);
    }
}

async function loadAthlete(event) {
    event?.preventDefault();
    const athleteId = els.lookupForm.elements.athleteId.value;
    if (!athleteId) {
        showToast("Sisesta sportlase ID");
        return;
    }

    setLoading(els.lookupForm, true);
    try {
        state.athlete = await request(`/athletes/${athleteId}`);
        renderAthlete();
        showToast("Sportlane avatud");
    } catch (error) {
        showToast(error.message);
    } finally {
        setLoading(els.lookupForm, false);
    }
}

async function addResult(event) {
    event.preventDefault();
    if (!state.athlete) {
        showToast("Vali sportlane");
        return;
    }

    const formData = new FormData(els.resultForm);
    setLoading(els.resultForm, true);

    try {
        await request(`/athletes/${state.athlete.id}/results`, {
            method: "POST",
            body: JSON.stringify({
                discipline: formData.get("discipline"),
                resultValue: Number(formData.get("resultValue"))
            })
        });
        els.resultForm.reset();
        await loadAthlete();
        showToast("Tulemus lisatud");
    } catch (error) {
        showToast(error.message);
    } finally {
        setLoading(els.resultForm, false);
    }
}

async function loadPoints() {
    if (!state.athlete) {
        showToast("Vali sportlane");
        return;
    }

    try {
        const points = await request(`/athletes/${state.athlete.id}/points`);
        showToast(`${points.name}: ${points.totalPoints} punkti`);
    } catch (error) {
        showToast(error.message);
    }
}

function escapeHtml(value) {
    return String(value ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll("\"", "&quot;")
        .replaceAll("'", "&#039;");
}

function escapeAttribute(value) {
    return escapeHtml(value).replaceAll("`", "&#096;");
}

els.disciplineForm.addEventListener("submit", createDiscipline);
els.refreshDisciplines.addEventListener("click", loadDisciplines);
els.athleteForm.addEventListener("submit", createAthlete);
els.lookupForm.addEventListener("submit", loadAthlete);
els.resultForm.addEventListener("submit", addResult);
els.loadPoints.addEventListener("click", loadPoints);

loadDisciplines();
