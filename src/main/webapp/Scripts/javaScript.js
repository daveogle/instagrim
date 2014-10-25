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

function hide(element)
{
    document.getElementById(element).style.display = 'none';
}

function validateComment(id)
{
    var i = document.getElementById(id).value;
    if (i === null || i === "") {
        alert("Comments cannot be left blank");
        return false;
    }
}

function editAccount(field)
{
    if (document.getElementById(field).style.display === 'none')
    {
        visable(field);
        visable(field + "Button");
    }
    else
    {
        hide(field);
        hide(field + "Button");
    }
}

function onClick(commentBox) {
    var div = document.getElementById(commentBox);
    if (div.style.display !== 'none') {
        div.style.display = 'none';
    }
    else {
        div.style.display = 'block';
    }
}

function deletePic(id, user)
{
    if (confirm("Are you sure you want to delete this image?") === true) {
        $.ajax({
            type: "DELETE",
            async: false,
            url: "/Instagrim/Image/" + user + "/" + id,
            success: function (msg) {
                alert("Image " + id + " Deleted: " + msg);
                location.reload();
            },
            error: function (msg) {
                alert("Error: " + msg);
            }
        });
    } else {
        document.getElementById(id).href = "#";
    }
}

function selectAvatar()
{
    document.getElementById('upavatar').click();
}
//
function updateAvatar()
{
    document.getElementById('avatarForm').submit();
}
function deleteComment(user, picId, commentId)
{
    if (confirm("Are you sure you want to delete this comment?") === true) {
        $.ajax({
            type: "DELETE",
            async: false,
            url: "/Instagrim/Comments/" + user + "/" + picId + "/" + commentId,
            success: function (msg) {
                alert("Comment " + commentId + " Deleted: " + msg);
                location.reload();
            },
            error: function (msg) {
                alert("Error: " + msg);
            }
        });
    } else {
        document.getElementById(id).href = "#";
    }
}