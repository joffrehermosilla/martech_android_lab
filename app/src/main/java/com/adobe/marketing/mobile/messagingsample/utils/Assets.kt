package com.adobe.marketing.mobile.messagingsample.util

object Assets {

    fun demoHtml(): String {

        return """
<!DOCTYPE html>
<html>

<head>

<meta name="viewport" content="width=device-width, initial-scale=1"/>

<style>

body{

margin:0;

background:#FF6B00;

font-family:Arial;

display:flex;

justify-content:center;

align-items:center;

height:100vh;

}

.card{

background:white;

width:90%;

max-width:340px;

border-radius:20px;

padding:20px;

text-align:center;

box-shadow:0 8px 25px rgba(0,0,0,.35);

}

img{

width:100%;

border-radius:12px;

}

button{

background:#1473E6;

color:white;

border:none;

padding:14px;

width:100%;

border-radius:10px;

font-size:18px;

margin-top:15px;

}

</style>

</head>

<body>

<div class="card">

<img src="https://images.unsplash.com/photo-1530143311094-34d807799e8f?q=80&w=600&auto=format&fit=crop">

<h2>Adobe Journey Optimizer</h2>

<p>Mensaje recibido desde Adobe.</p>

<button onclick="location.href='adbinapp://dismiss'">

Aceptar

</button>

</div>

</body>

</html>
        """.trimIndent()

    }

}