<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Garaje — Vehículos</title>
    <style>
        /* ======== ESTILO GENERAL ======== */
        :root {
            --azul-claro: #e8f1fc;
            --azul: #2a7de1;
            --azul-oscuro: #184c99;
            --azul-hover: #1e66c1;
            --gris: #f5f7fb;
            --texto: #1f2a37;
            --borde: #e5e7eb;
        }

        * { box-sizing: border-box; }
        body {
            font-family: 'Segoe UI', Roboto, system-ui, sans-serif;
            background: var(--gris);
            color: var(--texto);
            margin: 0;
            padding: 28px;
        }

        .container {
            max-width: 1100px;
            margin: 0 auto;
        }

        h1 {
            color: var(--azul-oscuro);
            border-bottom: 3px solid var(--azul);
            padding-bottom: 8px;
            margin: 0 0 18px 0;
        }

        h2 {
            color: var(--azul);
            margin: 24px 0 12px;
        }

        a, .btn-link {
            color: var(--azul);
            text-decoration: none;
        }
        a:hover, .btn-link:hover { color: var(--azul-hover); text-decoration: underline; }

        /* ======== MENSAJES ======== */
        .alert {
            padding: 10px 14px;
            border-radius: 8px;
            margin: 16px 0;
            width: 100%;
            max-width: 600px;
            font-weight: 600;
            border: 1px solid transparent;
        }
        .alert.ok {
            background: #d7f7e3;
            color: #0b7a45;
            border-color: #a8e0c3;
        }
        .alert.err {
            background: #fde4e4;
            color: #a60a0a;
            border-color: #f1b7b7;
        }

        /* ======== TARJETAS / FORM ======== */
        .card {
            background: #fff;
            border: 1px solid var(--borde);
            border-radius: 12px;
            box-shadow: 0 2px 6px rgba(0,0,0,0.05);
            padding: 18px 20px;
            margin-bottom: 22px;
        }

        form .row { margin-bottom: 12px; }

        label { font-weight: 600; color: var(--azul-oscuro); display:block; margin-bottom:6px; }
        input[type=text] {
            width: 100%;
            padding: 10px 12px;
            border-radius: 8px;
            border: 1px solid #cfd7e3;
            font-size: 15px;
            transition: border-color .2s, box-shadow .2s;
            background: #fff;
        }
        input[type=text]:focus {
            outline: none;
            border-color: var(--azul);
            box-shadow: 0 0 0 3px rgba(42,125,225,0.15);
        }

        .btn {
            display: inline-block;
            background: var(--azul);
            color: #fff;
            border: none;
            border-radius: 8px;
            padding: 9px 16px;
            cursor: pointer;
            font-size: 15px;
            transition: background .2s, transform .02s;
        }
        .btn:hover { background: var(--azul-hover); }
        .btn:active { transform: translateY(1px); }

        .btn-danger {
            background: #c62828;
        }
        .btn-danger:hover { background: #a31f1f; }

        .note { font-size: 12px; color: #667085; margin-top: 8px; }

        /* ======== TABLA ======== */
        .table-wrap { overflow-x: auto; }
        table {
            border-collapse: collapse;
            width: 100%;
            background: white;
            border-radius: 12px;
            overflow: hidden;
            border: 1px solid var(--borde);
            box-shadow: 0 2px 6px rgba(0,0,0,0.05);
        }

        th, td {
            border-bottom: 1px solid #eef2f7;
            padding: 12px 14px;
            text-align: left;
            vertical-align: top;
            font-size: 14px;
        }

        th {
            background: var(--azul-claro);
            color: var(--azul-oscuro);
            font-weight: 700;
            text-transform: uppercase;
            font-size: 12px;
            letter-spacing: .02em;
        }

        tbody tr:hover td { background: #f2f7ff; }

        .actions { white-space: nowrap; }
        .actions form { display: inline; margin-right: 6px; }

        .grid-2 {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 22px;
        }

        @media (max-width: 900px) {
            .grid-2 { grid-template-columns: 1fr; }
        }
    </style>
</head>
<body>
<div class="container">

    <h1>Garaje — Gestión de Vehículos</h1>

    <!-- MENSAJES -->
    <c:if test="${not empty ok}">
        <div class="alert ok">${ok}</div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert err">${error}</div>
    </c:if>

    <!--  FORMULARIOS -->
    <div class="grid-2">

        <!-- CREAR -->
        <div>
            <h2>Registrar nuevo vehículo</h2>
            <div class="card">
                <form action="vehiculos" method="post">
                    <input type="hidden" name="action" value="create"/>
                    <div class="row">
                        <label for="placa">Placa</label>
                        <input type="text" id="placa" name="placa" required />
                    </div>
                    <div class="row">
                        <label for="marca">Marca</label>
                        <input type="text" id="marca" name="marca" required />
                    </div>
                    <div class="row">
                        <label for="modelo">Modelo (año)</label>
                        <input type="text" id="modelo" name="modelo" placeholder="2020" required />
                    </div>
                    <div class="row">
                        <label for="color">Color</label>
                        <input type="text" id="color" name="color" placeholder="Rojo / Blanco / Negro / Azul / Gris" />
                    </div>
                    <div class="row">
                        <label for="propietario">Propietario</label>
                        <input type="text" id="propietario" name="propietario" required />
                    </div>
                    <div class="row">
                        <button type="submit" class="btn">Agregar vehículo</button>
                    </div>
                    <div class="note">
                        * Reglas: color permitido, placa única, propietario ≥ 5, marca/modelo/placa ≥ 3, antigüedad ≤ 20 años.
                    </div>
                </form>
            </div>
        </div>

        <!-- EDITAR  -->
        <c:if test="${not empty editVehiculo}">
            <div>
                <h2>Editar vehículo #${editVehiculo.id}</h2>
                <div class="card">
                    <form action="vehiculos" method="post">
                        <input type="hidden" name="action" value="update"/>
                        <input type="hidden" name="id" value="${editVehiculo.id}"/>
                        <div class="row">
                            <label for="eplaca">Placa</label>
                            <input type="text" id="eplaca" name="placa" value="${editVehiculo.placa}" required />
                        </div>
                        <div class="row">
                            <label for="emarca">Marca</label>
                            <input type="text" id="emarca" name="marca" value="${editVehiculo.marca}" required />
                        </div>
                        <div class="row">
                            <label for="emodelo">Modelo (año)</label>
                            <input type="text" id="emodelo" name="modelo" value="${editVehiculo.modelo}" required />
                        </div>
                        <div class="row">
                            <label for="ecolor">Color</label>
                            <input type="text" id="ecolor" name="color" value="${editVehiculo.color}" />
                        </div>
                        <div class="row">
                            <label for="eprop">Propietario</label>
                            <input type="text" id="eprop" name="propietario" value="${editVehiculo.propietario}" required />
                        </div>
                        <div class="row">
                            <button type="submit" class="btn">Actualizar</button>
                            <a href="vehiculos?action=list" class="btn-link" style="margin-left:12px;">Cancelar</a>
                        </div>
                        <div class="note">* Se aplican las mismas validaciones y unicidad de placa.</div>
                    </form>
                </div>
            </div>
        </c:if>

    </div>

    <!-- Listar -->
    <h2>Listado de vehículos</h2>
    <div class="table-wrap">
        <table>
            <thead>
            <tr>
                <th>ID</th>
                <th>Placa</th>
                <th>Marca</th>
                <th>Modelo</th>
                <th>Color</th>
                <th>Propietario</th>
                <th>Acciones</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${lista}" var="v">
                <tr>
                    <td>${v.id}</td>
                    <td>${v.placa}</td>
                    <td>${v.marca}</td>
                    <td>${v.modelo}</td>
                    <td>${v.color}</td>
                    <td>${v.propietario}</td>
                    <td class="actions">
                        <!-- Editar -->
                        <form action="vehiculos" method="get">
                            <input type="hidden" name="action" value="edit"/>
                            <input type="hidden" name="id" value="${v.id}"/>
                            <button type="submit" class="btn">Editar</button>
                        </form>

                        <!-- Eliminar-->
                        <form action="vehiculos" method="post" onsubmit="return confirm('¿Eliminar este vehículo?');">
                            <input type="hidden" name="action" value="delete"/>
                            <input type="hidden" name="id" value="${v.id}"/>
                            <button type="submit" class="btn btn-danger">Eliminar</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>

            <c:if test="${empty lista}">
                <tr>
                    <td colspan="7" style="text-align:center;color:#7b8794;">Sin registros disponibles.</td>
                </tr>
            </c:if>
            </tbody>
        </table>
    </div>

</div> 
</body>
</html>
