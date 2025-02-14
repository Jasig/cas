var CONST_CURRENT_VER = "development";
var CONST_SITE_TARGET_DIR = "/_site/";

function isDocumentationSiteViewedLocally() {
  return location.href.indexOf(CONST_SITE_TARGET_DIR) != -1;
}

function getActiveDocumentationVersionInView(returnBlankIfNoVersion) {
  var currentVersion = CONST_CURRENT_VER;
  var href = location.href;
  var index = isDocumentationSiteViewedLocally() ? href.indexOf(CONST_SITE_TARGET_DIR) : -1;

  if (index == -1) {
    var uri = new URI(document.location);

    if (uri.filename() != uri.segment(1) && uri.segment(1) != "developer") {
        currentVersion = uri.segment(1);
    } else if (returnBlankIfNoVersion) {
    	return "";
    }
  } else {
    href = href.substring(index + 7);
    index = href.indexOf("/");
    currentVersion = href.substring(0, index);
  }
  return currentVersion;
}


function loadSidebarForActiveVersion() {
  $("#sidebartoc").load("/cas/" + getActiveDocumentationVersionInView() + "/sidebar.html");
}

function hideDevelopmentVersionWarning() {
  var formattedVersion = getActiveDocumentationVersionInView(true);
  if (formattedVersion != CONST_CURRENT_VER) {
    $("#dev-doc-info").hide();
  }
}

function generateSidebarLinksForActiveVersion() {
  $('a').each(function() {
    var href = this.href;
    if (href.indexOf("$version") != -1) {
      href = href.replace("$version", "cas/" + getActiveDocumentationVersionInView());
      $(this).attr('href', href);
    }
  });
}

function generateToolbarIcons() {
  var CAS_REPO_URL_GITHUB = $('#forkme_banner').attr('href');
  var activeVersion = getActiveDocumentationVersionInView(true);
  
  var uri = new URI(document.location);
  var segments = uri.segment();
  var page = "";

  for (var i = 1; i < segments.length; i++) {
    page += segments[i] + "/";    
  }
  editablePage = page.replace(".html", ".md");
  editablePage = editablePage.replace(CONST_CURRENT_VER, "")
  editablePage = editablePage.replace(activeVersion, "")
  if (editablePage == "") {
  	editablePage = "index.md";
  }

  var imagesPath = "/cas/images/";
  if (isDocumentationSiteViewedLocally()) {
  	var loc = location.href;
  	var index = loc.indexOf(CONST_SITE_TARGET_DIR);
  	var uri2 = loc.substring(0, index + CONST_SITE_TARGET_DIR.length);
  	imagesPath = uri2 + "images/"
  }

  
  if (activeVersion != CONST_CURRENT_VER && activeVersion != "") {
    var linkToDev = "/cas/" + page.replace(activeVersion, CONST_CURRENT_VER);
    linkToDev = linkToDev.replace("html/", "html");
    
    $('#toolbarIcons').append("<a href='" + linkToDev +
      "'><img src='/cas/images/indev.png' alt='See the latest version of this page' title='See the latest version of this page'></a>");
  }

  var baseLink = CAS_REPO_URL_GITHUB;
  var editLink = "";
  var historyLink = "";
  var deleteLink = "";

  if (activeVersion == "") {
  	editLink = baseLink + "/edit/gh-pages/";
  	historyLink = baseLink + "/commits/gh-pages/";
  	deleteLink = baseLink + "/delete/gh-pages/";
  } else if (activeVersion != CONST_CURRENT_VER) {
  	editLink = baseLink + "/edit/" + activeVersion + "/cas-server-documentation/";
  	historyLink = baseLink + "/commits/" + activeVersion + "/cas-server-documentation/";
  	deleteLink = baseLink + "/delete/" + activeVersion + "/cas-server-documentation/";
  } else if (activeVersion == CONST_CURRENT_VER) {
  	editLink = baseLink + "/edit/master/cas-server-documentation/";
  	historyLink = baseLink + "/commits/master/cas-server-documentation/";
  	deleteLink = baseLink + "/delete/master/cas-server-documentation/";
  }

  editLink += editablePage;
 

  $('#toolbarIcons').append("<a target='_blank' href='" + editLink +
    "'><img src='" + imagesPath + "edit.png' alt='Edit with Github' title='Edit with Github'></a>");

  historyLink += editablePage;
  
  
  $('#toolbarIcons').append("<a target='_blank' href='" + historyLink +
    "'><img src='" + imagesPath + "history.png' alt='View commit history on Github' title='View commit history on Github'>");

  deleteLink += editablePage;
  
  
  $('#toolbarIcons').append("<a target='_blank' href='" + deleteLink +
    "'><img src='" + imagesPath + "delete.png' alt='Delete with Github' title='Delete with Github'>");
}

function generateTableOfContentsForPage() {
  $('#tableOfContents').append("<strong>Table of Contents</strong>");
  $('#tableOfContents').append("<ul>");
  
  $('h1, h2, h3').each(function() {
    if (this.id != "project_tagline" && this.id != "") {
      var tagName = $(this).prop("tagName").trim();
      var currentIndex = parseInt(tagName.substring(1, 2));
      
      var alignment = "";

      switch (currentIndex) {
        case 2:
          for (var i = 0; i < 3; i++) { alignment += "&nbsp;" }
          break;
        case 3:
          for (var i = 0; i < 6; i++) { alignment += "&nbsp;" }
          break;
      }
      
      $('#tableOfContents').append("<li><a href='#" + this.id + "'>" + alignment +
          this.textContent + "</a></li>");
    }
  });
  $('#tableOfContents').append("</ul>");
}

function guidGenerator() {
    var S4 = function() {
       return (((1+Math.random())*0x10000)|0).toString(16).substring(1);
    };
    return (S4()+S4()+"-"+S4()+"-"+S4()+"-"+S4()+"-"+S4()+S4()+S4());
}

function collapseCodeBlocks() {
	$(".highlight").each(function() {
		var id = guidGenerator();
		var showCodeButton = "<button type='button' class='btn btn-default' data-toggle='collapse' " +
							 "data-target='#" + id + "' aria-expanded='true'>" +
							 "<span class='glyphicon glyphicon-stats' aria-hidden='true'></span>" +
							 "&nbsp;Show Code</button>";
		$(this).attr("id", id);
		$(this).before(showCodeButton);
		$(this).addClass('collapse');
		$(this).prepend('<br>');
	});
}

function ensureBootrapIsLoaded() {
  if(typeof($.fn.modal) === 'undefined') {
    // require a minimum version of bootstrap
    $('head').prepend("<link rel='stylesheet' href='https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap.min.css'>");
    $('head').append("<script src='https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/js/bootstrap.min.js'></script>");
  }
}

$(function() {
  ensureBootrapIsLoaded();
  loadSidebarForActiveVersion();
  generateTableOfContentsForPage();
  generateToolbarIcons();
  collapseCodeBlocks();

  var formattedVersion = getActiveDocumentationVersionInView();
  if (formattedVersion != "" && formattedVersion.indexOf(CONST_CURRENT_VER) == -1) {
	formattedVersion = " (" + formattedVersion + ")"
  } else {
	formattedVersion = "";
  }
  hideDevelopmentVersionWarning();
  document.title = $("h1").first().text() + formattedVersion;
  
});
