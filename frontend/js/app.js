const API_BASE_URL = 'http://localhost:8080';

// Central State Management
const State = {
    currentRole: 'ADMIN', // Default role on startup
    activeTab: 'dashboard'
};

// Map roles to header values. For NONE, we omit the header.
const TokenMap = {
    'ADMIN': 'ADMIN',
    'AGENT': 'AGENT',
    'NONE': null
};

// Document Elements
document.addEventListener('DOMContentLoaded', () => {
    initApp();
});

function initApp() {
    // 1. Sidebar Tab Switching
    const navItems = document.querySelectorAll('.nav-item');
    navItems.forEach(item => {
        item.addEventListener('click', (e) => {
            e.preventDefault();
            const tabId = item.getAttribute('data-tab');
            switchTab(tabId);
        });
    });

    // 2. Role Selector Click Handlers
    const roleButtons = document.querySelectorAll('.role-btn');
    roleButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            roleButtons.forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            State.currentRole = btn.getAttribute('data-role');
            showToast(`Switched security context to: ${State.currentRole}`, 'info');
            refreshActiveTab();
        });
    });

    // 3. Search Inputs Event Listeners (with simple debounce)
    setupSearch('customer-search-input', searchCustomers);
    setupSearch('policy-search-input', searchPolicies);
    setupSearch('lead-search-input', searchLeads);

    // Initial load
    switchTab('dashboard');
}

// Setup debounce search
function setupSearch(inputId, searchFn) {
    const input = document.getElementById(inputId);
    let timeout = null;
    input.addEventListener('input', () => {
        clearTimeout(timeout);
        timeout = setTimeout(() => {
            searchFn(input.value.trim());
        }, 300);
    });
}

// Tab navigation handler
function switchTab(tabId) {
    State.activeTab = tabId;
    
    // Update nav links UI
    const navItems = document.querySelectorAll('.nav-item');
    navItems.forEach(item => {
        if (item.getAttribute('data-tab') === tabId) {
            item.classList.add('active');
        } else {
            item.classList.remove('active');
        }
    });

    // Update tab panes UI
    const panes = document.querySelectorAll('.tab-pane');
    panes.forEach(pane => {
        if (pane.id === `tab-${tabId}`) {
            pane.classList.add('active');
        } else {
            pane.classList.remove('active');
        }
    });

    // Update title
    const titles = {
        'dashboard': 'Dashboard Overview',
        'customers': 'Customer Management',
        'policies': 'Policy Management',
        'leads': 'Lead Management'
    };
    document.getElementById('page-title').innerText = titles[tabId] || 'Overview';

    // Refresh active data
    refreshActiveTab();
}

function refreshActiveTab() {
    if (State.activeTab === 'dashboard') {
        loadDashboardStats();
    } else if (State.activeTab === 'customers') {
        loadCustomers();
    } else if (State.activeTab === 'policies') {
        loadPolicies();
    } else if (State.activeTab === 'leads') {
        loadLeads();
    }
}

/* ==========================================================================
   Fetch API Helper Wrapper (Handles Authentication & Role Enforcement Errors)
   ========================================================================== */
async function apiCall(url, method = 'GET', body = null) {
    const headers = {
        'Content-Type': 'application/json'
    };

    // Inject custom X-Auth-Token header
    const token = TokenMap[State.currentRole];
    if (token) {
        headers['X-Auth-Token'] = token;
    }

    const options = {
        method,
        headers
    };

    if (body) {
        options.body = JSON.stringify(body);
    }

    try {
        const response = await fetch(API_BASE_URL + url, options);

        // Check for 204 No Content
        if (response.status === 204) {
            return null;
        }

        // Check for Auth Failures
        if (response.status === 401) {
            const err = await response.json();
            showToast(err.message || '401 Unauthorized: Missing or invalid token', 'error');
            throw new Error('401 Unauthorized');
        }

        if (response.status === 403) {
            const err = await response.json();
            showToast(err.message || '403 Forbidden: Access Denied', 'error');
            throw new Error('403 Forbidden');
        }

        const data = await response.json();

        // Check for Bad Request / Validation Failure Response payload
        if (!response.ok) {
            if (data.errors) {
                // Formatting field-level validation messages
                let validationMsg = Object.entries(data.errors)
                    .map(([field, msg]) => `${field}: ${msg}`)
                    .join('<br>');
                showToast(validationMsg, 'error');
            } else {
                showToast(data.message || 'Request failed', 'error');
            }
            throw new Error(data.message || 'API error');
        }

        return data;
    } catch (error) {
        console.error('Fetch error:', error);
        throw error;
    }
}

/* ==========================================================================
   Dashboard KPIs Loading
   ========================================================================== */
async function loadDashboardStats() {
    try {
        const customers = await apiCall('/api/customers') || [];
        const policies = await apiCall('/api/policies') || [];
        const leads = await apiCall('/api/leads') || [];

        document.getElementById('kpi-customers-count').innerText = customers.length;
        document.getElementById('kpi-policies-count').innerText = policies.length;
        document.getElementById('kpi-leads-count').innerText = leads.length;
    } catch (e) {
        // Suppress or clear numbers if unauthenticated
        document.getElementById('kpi-customers-count').innerText = '--';
        document.getElementById('kpi-policies-count').innerText = '--';
        document.getElementById('kpi-leads-count').innerText = '--';
    }
}

/* ==========================================================================
   1. Customer CRUD Operations
   ========================================================================== */
async function loadCustomers() {
    try {
        const customers = await apiCall('/api/customers') || [];
        renderCustomersTable(customers);
    } catch (e) {
        document.getElementById('customers-table-body').innerHTML = `<tr><td colspan="8" class="text-center text-danger">Failed to load customer data. Authentication required.</td></tr>`;
    }
}

function renderCustomersTable(customers) {
    const tbody = document.getElementById('customers-table-body');
    tbody.innerHTML = '';
    
    if (customers.length === 0) {
        tbody.innerHTML = `<tr><td colspan="8" style="text-align: center;">No customers found.</td></tr>`;
        return;
    }

    customers.forEach(cust => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${cust.id}</td>
            <td>${cust.firstName}</td>
            <td>${cust.lastName}</td>
            <td>${cust.email}</td>
            <td>${cust.phoneNumber}</td>
            <td>${cust.dateOfBirth}</td>
            <td><span class="badge ${cust.accountStatus === 'ACTIVE' ? 'badge-success' : 'badge-danger'}">${cust.accountStatus}</span></td>
            <td>
                <div class="action-btns">
                    <button class="btn-icon btn-edit" onclick="editCustomer(${cust.id})" title="Edit">✏️</button>
                    <button class="btn-icon btn-delete" onclick="deleteCustomer(${cust.id})" title="Delete">🗑️</button>
                </div>
            </td>
        `;
        tbody.appendChild(row);
    });
}

async function searchCustomers(keyword) {
    try {
        const customers = await apiCall(`/api/customers/search?keyword=${encodeURIComponent(keyword)}`) || [];
        renderCustomersTable(customers);
    } catch (e) {}
}

function openCustomerModal() {
    document.getElementById('customer-form').reset();
    document.getElementById('customer-id').value = '';
    document.getElementById('customer-modal-title').innerText = 'Add Customer';
    document.getElementById('customer-modal').classList.add('active');
}

function closeCustomerModal() {
    document.getElementById('customer-modal').classList.remove('active');
}

async function saveCustomer(e) {
    e.preventDefault();
    const id = document.getElementById('customer-id').value;
    const customerData = {
        firstName: document.getElementById('customer-firstName').value,
        lastName: document.getElementById('customer-lastName').value,
        email: document.getElementById('customer-email').value,
        phoneNumber: document.getElementById('customer-phoneNumber').value,
        dateOfBirth: document.getElementById('customer-dateOfBirth').value,
        accountStatus: document.getElementById('customer-accountStatus').value
    };

    try {
        if (id) {
            await apiCall(`/api/customers/${id}`, 'PUT', customerData);
            showToast('Customer updated successfully', 'success');
        } else {
            await apiCall('/api/customers', 'POST', customerData);
            showToast('Customer created successfully', 'success');
        }
        closeCustomerModal();
        loadCustomers();
    } catch (err) {}
}

async function editCustomer(id) {
    try {
        const cust = await apiCall(`/api/customers/${id}`);
        document.getElementById('customer-id').value = cust.id;
        document.getElementById('customer-firstName').value = cust.firstName;
        document.getElementById('customer-lastName').value = cust.lastName;
        document.getElementById('customer-email').value = cust.email;
        document.getElementById('customer-phoneNumber').value = cust.phoneNumber;
        document.getElementById('customer-dateOfBirth').value = cust.dateOfBirth;
        document.getElementById('customer-accountStatus').value = cust.accountStatus;
        
        document.getElementById('customer-modal-title').innerText = 'Edit Customer';
        document.getElementById('customer-modal').classList.add('active');
    } catch (err) {}
}

async function deleteCustomer(id) {
    if (!confirm('Are you sure you want to delete this customer? All associated policies will also be deleted.')) {
        return;
    }
    try {
        await apiCall(`/api/customers/${id}`, 'DELETE');
        showToast('Customer deleted successfully', 'success');
        loadCustomers();
    } catch (err) {}
}


/* ==========================================================================
   2. Policy CRUD Operations
   ========================================================================== */
async function loadPolicies() {
    try {
        const policies = await apiCall('/api/policies') || [];
        renderPoliciesTable(policies);
    } catch (e) {
        document.getElementById('policies-table-body').innerHTML = `<tr><td colspan="8" class="text-center text-danger">Failed to load policy data. Authentication required.</td></tr>`;
    }
}

function renderPoliciesTable(policies) {
    const tbody = document.getElementById('policies-table-body');
    tbody.innerHTML = '';
    
    if (policies.length === 0) {
        tbody.innerHTML = `<tr><td colspan="8" style="text-align: center;">No policies found.</td></tr>`;
        return;
    }

    policies.forEach(p => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${p.policyNumber}</td>
            <td>${p.policyName}</td>
            <td><span class="badge badge-success">${p.policyType}</span></td>
            <td>$${p.premiumAmount.toFixed(2)}</td>
            <td>${p.coverageTerm}</td>
            <td>${p.effectiveStartDate}</td>
            <td><span class="badge badge-warning">ID: ${p.customerId}</span> ${p.customerName}</td>
            <td>
                <div class="action-btns">
                    <button class="btn-icon btn-edit" onclick="editPolicy(${p.id})" title="Edit">✏️</button>
                    <button class="btn-icon btn-delete" onclick="deletePolicy(${p.id})" title="Delete">🗑️</button>
                </div>
            </td>
        `;
        tbody.appendChild(row);
    });
}

async function searchPolicies(keyword) {
    try {
        const policies = await apiCall(`/api/policies/search?keyword=${encodeURIComponent(keyword)}`) || [];
        renderPoliciesTable(policies);
    } catch (e) {}
}

async function openPolicyModal() {
    document.getElementById('policy-form').reset();
    document.getElementById('policy-id').value = '';
    document.getElementById('policy-modal-title').innerText = 'Add Policy';
    
    // Load customer dropdown before opening modal
    const loaded = await loadCustomerDropdownOptions();
    if (loaded) {
        document.getElementById('policy-modal').classList.add('active');
    }
}

async function loadCustomerDropdownOptions(selectedId = null) {
    const select = document.getElementById('policy-customerId');
    select.innerHTML = '<option value="">Loading customers...</option>';
    try {
        const customers = await apiCall('/api/customers') || [];
        select.innerHTML = '';
        if (customers.length === 0) {
            select.innerHTML = '<option value="">-- No Customers Registered Yet --</option>';
            showToast('You must create a customer first before adding a policy.', 'warning');
            return false;
        }
        customers.forEach(c => {
            const opt = document.createElement('option');
            opt.value = c.id;
            opt.innerText = `[ID: ${c.id}] ${c.firstName} ${c.lastName} (${c.email})`;
            if (selectedId && c.id === selectedId) {
                opt.selected = true;
            }
            select.appendChild(opt);
        });
        return true;
    } catch (e) {
        select.innerHTML = '<option value="">Failed to load customers</option>';
        return false;
    }
}

function closePolicyModal() {
    document.getElementById('policy-modal').classList.remove('active');
}

async function savePolicy(e) {
    e.preventDefault();
    const id = document.getElementById('policy-id').value;
    const policyData = {
        policyNumber: document.getElementById('policy-number').value,
        policyName: document.getElementById('policy-name').value,
        policyType: document.getElementById('policy-type').value,
        premiumAmount: parseFloat(document.getElementById('policy-premium').value),
        coverageTerm: parseInt(document.getElementById('policy-term').value),
        effectiveStartDate: document.getElementById('policy-startDate').value,
        customerId: parseInt(document.getElementById('policy-customerId').value)
    };

    try {
        if (id) {
            await apiCall(`/api/policies/${id}`, 'PUT', policyData);
            showToast('Policy updated successfully', 'success');
        } else {
            await apiCall('/api/policies', 'POST', policyData);
            showToast('Policy created successfully', 'success');
        }
        closePolicyModal();
        loadPolicies();
    } catch (err) {}
}

async function editPolicy(id) {
    try {
        const p = await apiCall(`/api/policies/${id}`);
        document.getElementById('policy-id').value = p.id;
        document.getElementById('policy-number').value = p.policyNumber;
        document.getElementById('policy-name').value = p.policyName;
        document.getElementById('policy-type').value = p.policyType;
        document.getElementById('policy-premium').value = p.premiumAmount;
        document.getElementById('policy-term').value = p.coverageTerm;
        document.getElementById('policy-startDate').value = p.effectiveStartDate;
        
        await loadCustomerDropdownOptions(p.customerId);
        
        document.getElementById('policy-modal-title').innerText = 'Edit Policy';
        document.getElementById('policy-modal').classList.add('active');
    } catch (err) {}
}

async function deletePolicy(id) {
    if (!confirm('Are you sure you want to delete this policy?')) {
        return;
    }
    try {
        await apiCall(`/api/policies/${id}`, 'DELETE');
        showToast('Policy deleted successfully', 'success');
        loadPolicies();
    } catch (err) {}
}


/* ==========================================================================
   3. Lead CRUD Operations
   ========================================================================== */
async function loadLeads() {
    try {
        const leads = await apiCall('/api/leads') || [];
        renderLeadsTable(leads);
    } catch (e) {
        document.getElementById('leads-table-body').innerHTML = `<tr><td colspan="7" class="text-center text-danger">Failed to load leads data. Authentication required.</td></tr>`;
    }
}

function renderLeadsTable(leads) {
    const tbody = document.getElementById('leads-table-body');
    tbody.innerHTML = '';
    
    if (leads.length === 0) {
        tbody.innerHTML = `<tr><td colspan="7" style="text-align: center;">No leads found.</td></tr>`;
        return;
    }

    leads.forEach(lead => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${lead.id}</td>
            <td>${lead.prospectName}</td>
            <td>${lead.contactInfo}</td>
            <td>${lead.referralSource || 'N/A'}</td>
            <td><span class="badge badge-success">${lead.leadStatus}</span></td>
            <td>${lead.assignedAgentName || 'Unassigned'}</td>
            <td>
                <div class="action-btns">
                    <button class="btn-icon btn-edit" onclick="editLead(${lead.id})" title="Edit">✏️</button>
                    <button class="btn-icon btn-delete" onclick="deleteLead(${lead.id})" title="Delete">🗑️</button>
                </div>
            </td>
        `;
        tbody.appendChild(row);
    });
}

async function searchLeads(keyword) {
    try {
        const leads = await apiCall(`/api/leads/search?keyword=${encodeURIComponent(keyword)}`) || [];
        renderLeadsTable(leads);
    } catch (e) {}
}

function openLeadModal() {
    document.getElementById('lead-form').reset();
    document.getElementById('lead-id').value = '';
    document.getElementById('lead-modal-title').innerText = 'Add Lead';
    document.getElementById('lead-modal').classList.add('active');
}

function closeLeadModal() {
    document.getElementById('lead-modal').classList.remove('active');
}

async function saveLead(e) {
    e.preventDefault();
    const id = document.getElementById('lead-id').value;
    const leadData = {
        prospectName: document.getElementById('lead-prospectName').value,
        contactInfo: document.getElementById('lead-contactInfo').value,
        referralSource: document.getElementById('lead-referralSource').value,
        leadStatus: document.getElementById('lead-leadStatus').value,
        assignedAgentName: document.getElementById('lead-assignedAgentName').value
    };

    try {
        if (id) {
            await apiCall(`/api/leads/${id}`, 'PUT', leadData);
            showToast('Lead updated successfully', 'success');
        } else {
            await apiCall('/api/leads', 'POST', leadData);
            showToast('Lead created successfully', 'success');
        }
        closeLeadModal();
        loadLeads();
    } catch (err) {}
}

async function editLead(id) {
    try {
        const lead = await apiCall(`/api/leads/${id}`);
        document.getElementById('lead-id').value = lead.id;
        document.getElementById('lead-prospectName').value = lead.prospectName;
        document.getElementById('lead-contactInfo').value = lead.contactInfo;
        document.getElementById('lead-referralSource').value = lead.referralSource || '';
        document.getElementById('lead-leadStatus').value = lead.leadStatus;
        document.getElementById('lead-assignedAgentName').value = lead.assignedAgentName || '';
        
        document.getElementById('lead-modal-title').innerText = 'Edit Lead';
        document.getElementById('lead-modal').classList.add('active');
    } catch (err) {}
}

async function deleteLead(id) {
    if (!confirm('Are you sure you want to delete this lead?')) {
        return;
    }
    try {
        await apiCall(`/api/leads/${id}`, 'DELETE');
        showToast('Lead deleted successfully', 'success');
        loadLeads();
    } catch (err) {}
}


/* ==========================================================================
   Toast Alerts Helper Functions
   ========================================================================== */
function showToast(message, type = 'success') {
    const container = document.getElementById('toast-container');
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    
    toast.innerHTML = `
        <div class="toast-content">${message}</div>
        <button class="toast-close" onclick="this.parentElement.remove()">×</button>
    `;
    
    container.appendChild(toast);
    
    // Self destruct toast after 5 seconds
    setTimeout(() => {
        if (toast.parentElement) {
            toast.remove();
        }
    }, 5000);
}
