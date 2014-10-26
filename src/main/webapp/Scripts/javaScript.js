/* 
 * @Title JavaScript File containing functions used in Instagrim project
 * @Author Dave Ogle
 */

/**
 * A Function to alert the user they are navgating away from a page
 * 
 * @param {type} mess - the message to display
 * @param {type} id - the id of the element in question
 * @param {type} lin - the link to navigate to if confirmed.
 */
function alertUser(mess, id, lin) {
    if (confirm(mess) === true) {
        document.getElementById(id).href = lin;
    } else {
        document.getElementById(id).href = "#";
    }
}

/**
 * Function to check if form inputs are valid 
 * 
 * @param {type} name - the name of the form
 * @param {type} field1 - the first input to check
 * @param {type} field2 - a second input to check 
 * @returns {Boolean} return if valid
 */
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

/**
 * Function to show an element that is hidden
 * @param {type} element - the element to display
 * @returns {undefined}
 */
function visable(element)
{
    document.getElementById(element).style.display = 'block';
}

/**
 * Function to hide an element that is visable
 * @param {type} element - the element to hide
 * @returns {undefined}
 */
function hide(element)
{
    document.getElementById(element).style.display = 'none';
}

/**
 * Function to validate a comment
 * @param {type} id = id of comment
 * @returns {Boolean} if valid
 */
function validateComment(id)
{
    var i = document.getElementById(id).value;
    if (i === null || i === "") {
        alert("Comments cannot be left blank");
        return false;
    }
}

/**
 * Function to show inputs for editing the account details
 * @param {type} field - field to edit
 * @returns {undefined}
 */
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

/**
 * function to show comments submit button
 * @param {type} commentBox
 * @returns {undefined}
 */
function onClick(commentBox) {
    var div = document.getElementById(commentBox);
    if (div.style.display !== 'none') {
        div.style.display = 'none';
    }
    else {
        div.style.display = 'block';
    }
}

/**
 * Function confirm a user wants to delete an image then call the HTTP doDelete method in the servlett
 * @param {type} id
 * @param {type} user
 * @returns {undefined}
 */
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

/**
 * Fnction to open dialouge box to select an avatar pic when pic is clicked
 * @returns {undefined}
 */
function selectAvatar()
{
    document.getElementById('upavatar').click();
}

/**
 * Function to submit the selected avatar as a form
 * @returns {undefined}
 */
function updateAvatar()
{
    document.getElementById('avatarForm').submit();
}

/**
 * Function to confirm a user wishes to delete a comment and then call the doDelete method in the Comment Servlette
 * @param {type} user
 * @param {type} picId
 * @param {type} commentId
 * @returns {undefined}
 */
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
            error: function () {
                alert("Error: You must be the picture owner" );
            }
        });
    } else {
        document.getElementById(id).href = "#";
    }
}