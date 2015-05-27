	window.addEventListener('load', function() {
		registerEvents();
	});

	function registerEvents() {
		var elWriteForm = document.querySelector('.answerWrite input[type=submit]');
		var elComments = document.querySelector('#comments');

		elWriteForm.addEventListener('click', writeAnswer, false);
		elComments.addEventListener('click', deleteAnswer, false);
	}

	function deleteAnswer(e) {
		e.preventDefault();
		if (e.target.nodeName !== 'A') {
			return;
		}

		var ids = e.target.getAttribute("data-ids").split('_');
		var url = "/api/questions/" + ids[0] + "/answers/" + ids[1];

		var request = new XMLHttpRequest();
		request.open("DELETE", url, true);
		request.setRequestHeader("Content-type", "application/x-www-form-urlencoded");

		request.onreadystatechange = function() {
			if (request.readyState == 4 && request.status == 200) {
				var result = JSON.parse(request.responseText);
				if (result.status) {
					// delete current NODE.
					var elCurrent = e.target.parentElement.parentElement;
					elCurrent.parentNode.removeChild(elCurrent);
				}
			}
		}
		request.send();
	}

	function writeAnswer(e) {
		e.preventDefault();

		var answerForm = e.currentTarget.form;
		var url = "/api/questions/" + answerForm[0].value + "/answers";
		var params = "writer=" + answerForm[1].value + "&contents=" + answerForm[2].value;

		var request = new XMLHttpRequest();
		request.open("POST", url, true);
		request.setRequestHeader("Content-type", "application/x-www-form-urlencoded");

		request.onreadystatechange = function() {
			if (request.readyState == 4 && request.status == 200) {
				var result = request.responseText;
				result = JSON.parse(result);
				if(result.status === false) {
					alert(result.errorMessage);
					return;
				}
				location.reload(true);
			}
		}

		request.send(params);
	}

