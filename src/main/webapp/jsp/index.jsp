<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Order & Chat Management System</title>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
            background: white;
            border-radius: 10px;
            box-shadow: 0 10px 50px rgba(0, 0, 0, 0.3);
            overflow: hidden;
        }
        .header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 30px;
            text-align: center;
        }
        .header h1 {
            font-size: 2.5em;
            margin-bottom: 10px;
        }
        .content {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 30px;
            padding: 30px;
        }
        .section {
            background: #f8f9fa;
            padding: 25px;
            border-radius: 8px;
            border-left: 5px solid #667eea;
        }
        .section h2 {
            color: #333;
            margin-bottom: 20px;
            font-size: 1.5em;
        }
        .form-group {
            margin-bottom: 15px;
        }
        label {
            display: block;
            margin-bottom: 5px;
            color: #555;
            font-weight: 600;
        }
        input, textarea, select {
            width: 100%;
            padding: 10px;
            border: 2px solid #ddd;
            border-radius: 5px;
            font-size: 14px;
            transition: border-color 0.3s;
        }
        input:focus, textarea:focus, select:focus {
            outline: none;
            border-color: #667eea;
            background: #f0f4ff;
        }
        button {
            background: #667eea;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 14px;
            font-weight: 600;
            transition: background 0.3s;
            margin-right: 10px;
            margin-top: 10px;
        }
        button:hover {
            background: #764ba2;
        }
        button.danger {
            background: #dc3545;
        }
        button.danger:hover {
            background: #c82333;
        }
        .order-list {
            max-height: 400px;
            overflow-y: auto;
        }
        .order-item {
            background: white;
            padding: 12px;
            margin-bottom: 10px;
            border-radius: 5px;
            border-left: 4px solid #667eea;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .order-info {
            flex: 1;
        }
        .order-id {
            color: #667eea;
            font-weight: 700;
        }
        .order-details {
            color: #777;
            font-size: 13px;
            margin-top: 5px;
        }
        .order-actions {
            display: flex;
            gap: 5px;
        }
        .order-actions button {
            padding: 5px 10px;
            font-size: 12px;
            margin: 0;
        }
        .response-box {
            background: white;
            border: 2px solid #ddd;
            border-radius: 5px;
            padding: 15px;
            margin-top: 15px;
            max-height: 300px;
            overflow-y: auto;
            display: none;
        }
        .response-box.show {
            display: block;
        }
        .response-title {
            font-weight: 700;
            color: #667eea;
            margin-bottom: 10px;
        }
        .response-text {
            color: #555;
            line-height: 1.6;
        }
        .loading {
            display: none;
            text-align: center;
            color: #667eea;
            font-weight: 600;
        }
        .loading.show {
            display: block;
        }
        .alert {
            padding: 12px;
            border-radius: 5px;
            margin-bottom: 15px;
            display: none;
        }
        .alert.show {
            display: block;
        }
        .alert.success {
            background: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        .alert.error {
            background: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        .button-group {
            display: flex;
            gap: 10px;
            flex-wrap: wrap;
        }
        @media (max-width: 900px) {
            .content {
                grid-template-columns: 1fr;
            }
        }
        .edit-form {
            display: none;
            background: white;
            padding: 15px;
            border-radius: 5px;
            margin-top: 15px;
        }
        .edit-form.show {
            display: block;
        }
        .close-btn {
            float: right;
            background: #999;
            color: white;
            padding: 5px 10px;
            border-radius: 3px;
            cursor: pointer;
            font-size: 12px;
        }
        .close-btn:hover {
            background: #666;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>📦 Order & Chat Management</h1>
            <p>Create, Read, Update, Delete Orders & Ask Questions</p>
        </div>

        <div class="content">
            <!-- CRUD Section -->
            <div class="section">
                <h2>Order Management (CRUD)</h2>

                <div id="crudAlert" class="alert"></div>

                <!-- Create Form -->
                <div class="form-group">
                    <label>Order Details:</label>
                    <input type="text" id="orderDetails" placeholder="Enter order details" maxlength="200">
                </div>
                <div class="button-group">
                    <button onclick="createOrder()">➕ Create Order</button>
                    <button onclick="refreshOrders()">🔄 Refresh List</button>
                </div>

                <!-- Orders List -->
                <div style="margin-top: 25px;">
                    <h3 style="color: #555; margin-bottom: 15px;">All Orders:</h3>
                    <div id="loadingOrders" class="loading">⏳ Loading orders...</div>
                    <div id="ordersContainer" class="order-list"></div>
                </div>

                <!-- Edit Form -->
                <div id="editForm" class="edit-form">
                    <span class="close-btn" onclick="closeEditForm()">Close</span>
                    <h4 style="color: #667eea; margin-bottom: 15px;">Edit Order</h4>
                    <div class="form-group">
                        <label>Order ID:</label>
                        <input type="text" id="editOrderId" readonly style="background: #e9ecef;">
                    </div>
                    <div class="form-group">
                        <label>New Details:</label>
                        <input type="text" id="editOrderDetails" placeholder="Enter new details" maxlength="200">
                    </div>
                    <button onclick="saveOrderUpdate()">💾 Save Changes</button>
                </div>
            </div>

            <!-- Chat Section -->
            <div class="section">
                <h2>💬 Ask AI Question</h2>

                <div id="chatAlert" class="alert"></div>

                <!-- Chat Form -->
                <div class="form-group">
                    <label>Question:</label>
                    <textarea id="questionText" placeholder="Ask any question..." rows="4" maxlength="500"></textarea>
                </div>

                <div class="form-group">
                    <label>Conversation ID (Optional):</label>
                    <input type="text" id="conversationId" placeholder="Leave blank or enter ID to maintain context" maxlength="100">
                </div>

                <div class="button-group">
                    <button onclick="askQuestion()">🚀 Ask Question</button>
                    <button onclick="resetChat()">🔄 Reset</button>
                </div>

                <!-- Response Box -->
                <div id="responseBox" class="response-box">
                    <div class="response-title">AI Response:</div>
                    <div id="responseText" class="response-text"></div>
                </div>

                <div id="loadingChat" class="loading">⏳ Waiting for AI response...</div>
            </div>
        </div>
    </div>

    <script>
        // Utility Functions
        function showAlert(message, type, alertId) {
            const alertBox = document.getElementById(alertId);
            alertBox.textContent = message;
            alertBox.className = 'alert show ' + type;
            setTimeout(() => {
                alertBox.classList.remove('show');
            }, 4000);
        }

        // Order CRUD Functions
        function createOrder() {
            const details = document.getElementById('orderDetails').value.trim();
            if (!details) {
                showAlert('❌ Please enter order details', 'error', 'crudAlert');
                return;
            }

            $.ajax({
                url: '/order',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({ id: 0, details: details }),
                success: function(response) {
                    showAlert('✅ Order created successfully (ID: ' + response.id + ')', 'success', 'crudAlert');
                    document.getElementById('orderDetails').value = '';
                    refreshOrders();
                },
                error: function() {
                    showAlert('❌ Error creating order', 'error', 'crudAlert');
                }
            });
        }

        function refreshOrders() {
            document.getElementById('loadingOrders').classList.add('show');
            $.ajax({
                url: '/order/all',
                type: 'GET',
                success: function(orders) {
                    document.getElementById('loadingOrders').classList.remove('show');
                    const container = document.getElementById('ordersContainer');

                    if (orders.length === 0) {
                        container.innerHTML = '<p style="color: #999; text-align: center;">No orders yet</p>';
                        return;
                    }

                    container.innerHTML = orders.map(order => `
                        <div class="order-item">
                            <div class="order-info">
                                <div class="order-id">Order #${order.id}</div>
                                <div class="order-details">${escapeHtml(order.details)}</div>
                            </div>
                            <div class="order-actions">
                                <button onclick="editOrder(${order.id}, '${escapeHtml(order.details)}')">✏️ Edit</button>
                                <button class="danger" onclick="deleteOrder(${order.id})">🗑️ Delete</button>
                            </div>
                        </div>
                    `).join('');
                },
                error: function() {
                    document.getElementById('loadingOrders').classList.remove('show');
                    showAlert('❌ Error loading orders', 'error', 'crudAlert');
                }
            });
        }

        function editOrder(id, details) {
            document.getElementById('editOrderId').value = id;
            document.getElementById('editOrderDetails').value = details;
            document.getElementById('editForm').classList.add('show');
        }

        function closeEditForm() {
            document.getElementById('editForm').classList.remove('show');
        }

        function saveOrderUpdate() {
            const id = document.getElementById('editOrderId').value;
            const details = document.getElementById('editOrderDetails').value.trim();

            if (!details) {
                showAlert('❌ Please enter order details', 'error', 'crudAlert');
                return;
            }

            $.ajax({
                url: '/order/' + id,
                type: 'PUT',
                contentType: 'application/json',
                data: JSON.stringify({ id: id, details: details }),
                success: function() {
                    showAlert('✅ Order updated successfully', 'success', 'crudAlert');
                    closeEditForm();
                    refreshOrders();
                },
                error: function() {
                    showAlert('❌ Error updating order', 'error', 'crudAlert');
                }
            });
        }

        function deleteOrder(id) {
            if (!confirm('Are you sure you want to delete Order #' + id + '?')) {
                return;
            }

            $.ajax({
                url: '/order/' + id,
                type: 'DELETE',
                success: function() {
                    showAlert('✅ Order deleted successfully', 'success', 'crudAlert');
                    refreshOrders();
                },
                error: function() {
                    showAlert('❌ Error deleting order', 'error', 'crudAlert');
                }
            });
        }

        // Chat Functions
        function askQuestion() {
            const question = document.getElementById('questionText').value.trim();
            const conversationId = document.getElementById('conversationId').value.trim() || 'default';

            if (!question) {
                showAlert('❌ Please enter a question', 'error', 'chatAlert');
                return;
            }

            document.getElementById('loadingChat').classList.add('show');
            document.getElementById('responseBox').classList.remove('show');

            $.ajax({
                url: '/order/askQuestion',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({ questionText: question, converSationId: conversationId }),
                success: function(response) {
                    document.getElementById('loadingChat').classList.remove('show');
                    document.getElementById('responseText').innerHTML = response.response;
                    document.getElementById('responseBox').classList.add('show');
                    showAlert('✅ Response received', 'success', 'chatAlert');
                },
                error: function() {
                    document.getElementById('loadingChat').classList.remove('show');
                    showAlert('❌ Error getting response from AI', 'error', 'chatAlert');
                }
            });
        }

        function resetChat() {
            document.getElementById('questionText').value = '';
            document.getElementById('conversationId').value = '';
            document.getElementById('responseBox').classList.remove('show');
            document.getElementById('loadingChat').classList.remove('show');
        }

        // Utility to escape HTML
        function escapeHtml(text) {
            const div = document.createElement('div');
            div.textContent = text;
            return div.innerHTML;
        }

        // Load orders on page load
        $(document).ready(function() {
            refreshOrders();
        });
    </script>
</body>
</html>

