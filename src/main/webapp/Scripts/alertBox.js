/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
function alertUser(mess, id, lin) {
    if (confirm(mess) === true) {
        document.getElementById(id).href = lin;
    } else {
        document.getElementById(id).href = "#";
    }
}

function validateForm(name, field1, field2) {
    var i = document.forms[name][field1].value;
    var j = document.forms[name][field2].value;
    var errField;
    var valid = true;
    if (i === null || i === "") {
        errField = field1;
        valid = false;
    }
    else if (j === null || j === "")
    {
        errField = field2;
        valid = false;
    }
    if (!valid)
    {
        alert("The field " + errField + " is required and cannot be left blank");
    }
    return valid;
}

function visable(element)
{
    document.getElementById(element).style.display = 'block';
}