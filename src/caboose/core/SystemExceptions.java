/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package caboose.core;

/**
 *
 * @author Jody
 */
public interface SystemExceptions {
     //Exception Handling Constants
    final String VIEW_MODEL_ERROR = "<html><head><title>CABOOSE ERROR</title><head><body>The exception view model, content, or tile mappings were not found.</body></html>";
    final String STENCIL_CONTENT_TYPE_ERROR = "Stencil content type is null or empty.";
    final String STENCIL_FILE_NAME_ERROR = "Stencil name is null or empty.";
    final String TILE_RENDERER_MAPPINGS_ERROR = "The mapping between tiles and render methods is null.";
    final String STENCIL_CONTENT_ERROR = "The stencil content of the view stencil is empty.";
    final String QUERY_STRING_ERROR = "The input key-value map is empty. Could not interpret the query string.";
    final String VIEW_IDENTIFIER_ERROR = "The view identifier is null or empty.";
    final String VIEW_MODEL_MISSING_ERROR = "The view model was not found.";
    final String CLASS_METHOD_ERROR = "The class or method name for the tile renderer was null or empty.";
    final String FORMATTED_CLASS_METHOD_ERROR = "<p>" + CLASS_METHOD_ERROR + "</p>"; ;    
    final String DIRECTORY_ERROR = "The application view directory map is empty.";
    final String EXCEPTION_VIEW = "exception";
    final String EXCEPTION_KEY = "exception_message";
}


