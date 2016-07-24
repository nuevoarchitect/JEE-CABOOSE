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

/**
 * The CAB is the kernel of the CABOOSE general purpose servlet. Implicit
 * Contract Requirements and Guidelines: The CAB contracts with support classes
 * that have rendering methods which return String content. These methods also
 * must have the signature public String methodName(
 * java.util.HashMap<String,String> aMapParameter ) by implicit contract. If
 * they do not, the reflection activities which automatically invoke them will
 * fail since they cannot find the requested method. Class, method, tile
 * replacement, and stencil filename identifiers are all stored in a file called
 * directory.xml which contains a mapping between each item.
 *
 * @author Jody
 */
public class Engine implements SystemExceptions, SystemStandards {

    //A data structure for holding a description of the page, stencil, and tile 
    //directory. It is keyed by the page name. Each value in the structure is a
    //ViewModel. See the description of this inner class for information on its
    //content and structure.
    java.util.HashMap<String, ViewModel> aDirectoryMap = null;

    //private inner class representing a model of a single view which the 
    //controller can return.
    private class ViewModel {
        //The stencil filename.

        private String theStencilFileName = null;
        private String theStencilContentType = null;
        //A data structure which holds the tile identifier as a key and the name
        //of the class and method for rendering the the tile as a 
        //comma seperated value
        private java.util.HashMap<String, String> theTileRendererMappings = null;

        //This method assigns the stencil content type
        void settingTheStencilContentType(String aStencilContentType) throws Exception {
            //Opening Contract
            //The stencil contet type is not a null or empty string.            
            if (aStencilContentType == null || aStencilContentType.length() == 0) {
                throw new Exception(STENCIL_CONTENT_TYPE_ERROR);
            }
            theStencilContentType = aStencilContentType;
            //Closing Contract (Implicit)
            //The stencil content type is a non-empty string.
        }

        //This method assigns the stencil filename
        void settingTheStencilFileName(String aStencilFileName) throws Exception {
            //Opening Contract
            //The stencil name is not a null or empty string.            
            if (aStencilFileName == null || aStencilFileName.length() == 0) {
                throw new Exception(STENCIL_FILE_NAME_ERROR);
            }
            theStencilFileName = aStencilFileName;
            //Closing Contract (Implicit)
            //The stencil name is a non-empty string.
        }

        //This method assigns the tile renderer mappings
        void settingTheTileRendererMappings(java.util.HashMap<String, String> aSetOfTileRendererMappings) throws Exception {
            //Opening Contract
            //The set of mappings between tiles and renderer methods is not null
            //It might be of size zero if there does not exist any replacment tiles
            //within the stencil.
            if (aSetOfTileRendererMappings == null) {
                throw new Exception(TILE_RENDERER_MAPPINGS_ERROR);
            }
            theTileRendererMappings = aSetOfTileRendererMappings;
            //Closing Contract (Implicit)
            //The tile renderer mappings is not null. They might be empty.
        }

        //This method returns the stencil file name
        String gettingTheStencilFileName() throws Exception {
            //Invariant Opening Contract
            //The stencil name is not a null or empty string.            
            if (theStencilFileName == null || theStencilFileName.length() == 0) {
                throw new Exception(STENCIL_FILE_NAME_ERROR);
            }
            return (theStencilFileName);
        }

        //This method returns the stencil content type
        String gettingTheStencilContentType() throws Exception {
            //Invariant Opening Contract
            //The stencil content type is not a null or empty string.            
            if (theStencilContentType == null || theStencilContentType.length() == 0) {
                throw new Exception(STENCIL_CONTENT_TYPE_ERROR);
            }
            return (theStencilContentType);
        }

        //This method retruns the tile renderer mappings data structure
        java.util.HashMap<String, String> gettingTheTileRendererMappings() throws Exception {
            //Invariant Opening Contract
            //The tile renderer mappings are not null. They might be empty.
            if (theTileRendererMappings == null) {
                throw new Exception(TILE_RENDERER_MAPPINGS_ERROR);
            }
            return (theTileRendererMappings);
        }
    }

    //This method extracts the content of a stencil from the file system based 
    //upon a file name. In the future this method will need adapting so it 
    //retrieves the proper file in a web server's file system using 
    //the servlet context.
    private String gettingTheResponseStencil(javax.servlet.ServletContext theServletContext, String aStencilFileName) throws Exception {
        //Opening Contract
        //The file name of the view stencil is not a null or empty string.
        if (aStencilFileName == null || aStencilFileName.length() == 0) {
            throw new Exception(STENCIL_FILE_NAME_ERROR);
        }
        java.io.BufferedReader aStencilReader = new java.io.BufferedReader(new java.io.FileReader(theServletContext.getRealPath(aStencilFileName)));
        String theStencilContent = "", theNextLine = null;
        while ((theNextLine = aStencilReader.readLine()) != null) {
            theStencilContent += theNextLine;
        }
        //Closing Contract
        //The stencil content of the view in not its initial value of an empty string.
        if (theStencilContent.length() == 0) {
            throw new Exception(STENCIL_CONTENT_ERROR);
        }
        return (theStencilContent);
    }

    //When an exception has occurred, this method populates a view stencil with 
    //the content generated by dynamically invoking rendering methods and 
    //replacing their associated user-defined tile identifiers 
    //( a.k.a stencil replacement variables ). Notice that this method does not
    //throw an exception upward.
    //See the contract guideline in the header comments of this document.
    String preparingTheExceptionResponse(javax.servlet.ServletContext theServletContext, javax.servlet.http.HttpServletRequest theHTTPServletRequest, javax.servlet.http.HttpServletResponse theHTTPServletResponse) {
        String aReplacementString = "";
        String aSupplementalExceptionMessage = "";
        String theStencilContent = VIEW_MODEL_ERROR;
        ViewModel aViewModel = null;
        String[] aTileClassAndMethod = null;

        //Opening Contract (Implicit)
        //The size of the request map is zero or larger 
        aViewModel = (ViewModel) aDirectoryMap.get(EXCEPTION_VIEW);

        //Intermediate Contract
        //The view model cannot be null.
        if (aViewModel == null) {
            return (theStencilContent);
        }

        try {
            theStencilContent = gettingTheResponseStencil(theServletContext, aViewModel.gettingTheStencilFileName());
        } catch (Exception e) {
            return (theStencilContent);
        }

        //Intermediate Contract (Implicit)
        //The stencil content of the view in not an empty string.
        java.util.HashMap<String, String> someTileRenderMappings = null;

        try {
            someTileRenderMappings = aViewModel.gettingTheTileRendererMappings();
        } catch (Exception e) {
            return (theStencilContent);
        }

        //Intermediate Contract (Implicit)
        //The tile renderer mappings are not null. They might be empty.
        for (String aTileId : someTileRenderMappings.keySet()) {

            //Intermediate Contract Within Evaluation (.get) (Implicit)
            //The tile class and method is a non-empty string which contains a single
            //comma as a CSV string.
            aTileClassAndMethod = ((String) someTileRenderMappings.get(aTileId)).split(",");

            //Intermediate Contract
            //The class and method names for the tile renderer cannot be null or empty.
            if (aTileClassAndMethod[CLASS_INDEX] == null || aTileClassAndMethod[METHOD_INDEX] == null || aTileClassAndMethod[CLASS_INDEX].length() == 0 || aTileClassAndMethod[METHOD_INDEX].length() == 0) {
                aSupplementalExceptionMessage += FORMATTED_CLASS_METHOD_ERROR;
            }

            try {
                //Start Reflection Activities
                Class aClass = Class.forName(aTileClassAndMethod[CLASS_INDEX]);
                Class[] theFullyQualifiedParameterTypeClasses = new Class[2];
                theFullyQualifiedParameterTypeClasses[0] = theHTTPServletRequest.getClass();
                theFullyQualifiedParameterTypeClasses[1] = theHTTPServletResponse.getClass();
                Object[] theParameterObjectList = new Object[2];
                theParameterObjectList[0] = theHTTPServletRequest;
                theParameterObjectList[1] = theHTTPServletResponse;
                aReplacementString = (String) (aClass.getMethod(aTileClassAndMethod[METHOD_INDEX], theFullyQualifiedParameterTypeClasses)).invoke((aClass.getConstructor()).newInstance(), theParameterObjectList);
                //End Reflection Activities                         
                theHTTPServletResponse.setContentType(aViewModel.gettingTheStencilContentType());
            } catch (Exception e) {
                aReplacementString = "<p>" + e.getMessage() + "</p>";
            }

            theStencilContent = theStencilContent.replace(aTileId, aReplacementString + aSupplementalExceptionMessage);

        }

        //Closing Contract (Implicit)
        //The class and method for the given name exist among the controller application bundle.      
        return (theStencilContent);
    }

    //This method populates a view stencil with the content generated by 
    //dynamically invoking reendering methods and replacing their associated
    //user-defined tile identifiers ( a.k.a stencil replacement variables ).
    //See the contract guideline in the header comments of this document.
    String preparingTheSuccessfulResponse(String theViewId, javax.servlet.ServletContext theServletContext, javax.servlet.http.HttpServletRequest theHTTPServletRequest, javax.servlet.http.HttpServletResponse theHTTPServletResponse) throws Exception {
        
        //Opening Contract
        //The id of the view cannot be null or empty 
        if (theViewId == null || theViewId.length() == 0) {
            throw new Exception(VIEW_IDENTIFIER_ERROR);
        }

        javax.servlet.http.HttpSession aHTTPSession = theHTTPServletRequest.getSession();
        if (aHTTPSession.getAttribute("VIEW_DIRECTORY") == null) {
            aDirectoryMap = populatingTheDirectoryMap(theServletContext);
            aHTTPSession.setAttribute("VIEW_DIRECTORY", aDirectoryMap);
        } else {
            aDirectoryMap = (java.util.HashMap<String, ViewModel>) aHTTPSession.getAttribute("VIEW_DIRECTORY");
        }
        
        //Opening Contract (Implicit)
        //The size of the request map is zero or larger 
        ViewModel aViewModel = (ViewModel) aDirectoryMap.get(theViewId);

        //Intermediate Contract
        //The view model cannot be null.
        if (aViewModel == null) {
            throw new Exception(VIEW_MODEL_MISSING_ERROR);
        }

        String[] aTileClassAndMethod = null;

        String theStencilContent = gettingTheResponseStencil(theServletContext, aViewModel.gettingTheStencilFileName());

        //Intermediate Contract (Implicit)
        //The stencil content of the view in not an empty string.
        java.util.HashMap<String, String> someTileRenderMappings = aViewModel.gettingTheTileRendererMappings();

        //Intermediate Contract (Implicit)
        //The tile renderer mappings are not null. They might be empty.
        for (String aTileId : someTileRenderMappings.keySet()) {

            //Intermediate Contract Within Evaluation (.get) (Implicit)
            //The tile class and method is a non-empty string which contains a single
            //comma as a CSV string.
            aTileClassAndMethod = ((String) someTileRenderMappings.get(aTileId)).split(",");

            //Intermediate Contract
            //The class and method names for the tile renderer cannot be null or empty.
            if (aTileClassAndMethod[CLASS_INDEX] == null || aTileClassAndMethod[METHOD_INDEX] == null || aTileClassAndMethod[CLASS_INDEX].length() == 0 || aTileClassAndMethod[METHOD_INDEX].length() == 0) {
                throw new Exception(CLASS_METHOD_ERROR);
            }

            //Start Reflection Activities
            Class aClass = Class.forName(aTileClassAndMethod[CLASS_INDEX]);
            Class[] theFullyQualifiedParameterTypeClasses = new Class[2];
            theFullyQualifiedParameterTypeClasses[0] = theHTTPServletRequest.getClass();
            theFullyQualifiedParameterTypeClasses[1] = theHTTPServletResponse.getClass();
            Object[] theParameterObjectList = new Object[2];
            theParameterObjectList[0] = theHTTPServletRequest;
            theParameterObjectList[1] = theHTTPServletResponse;
            theStencilContent = theStencilContent.replace(aTileId, (String) (aClass.getMethod(aTileClassAndMethod[METHOD_INDEX], theFullyQualifiedParameterTypeClasses)).invoke((aClass.getConstructor()).newInstance(), theParameterObjectList));
            //End Reflection Activities                         

            theHTTPServletResponse.setContentType(aViewModel.gettingTheStencilContentType());
        }

        //Closing Contract (Implicit)
        //The class and method for the given name exist among the controller application bundle.      
        return (theStencilContent);
    }

    private org.w3c.dom.Document gettingTheDirectoryXMLDocument(javax.servlet.ServletContext theServletContext) throws Exception {

        javax.xml.parsers.DocumentBuilderFactory theDocumentBuilderFactory
                = javax.xml.parsers.DocumentBuilderFactory.newInstance();
        javax.xml.parsers.DocumentBuilder theDocumentBuilder = theDocumentBuilderFactory.newDocumentBuilder();
        //Intermediate Contract (implicit)
        //The argument for the java.io.File constructor is the canonical name
        //for all CABOOSE bundles and is XML. (It will differ in the final version).
        java.io.File aFile = new java.io.File(theServletContext.getRealPath(STANDARD_DIRECTORY_SOURCE));
        org.w3c.dom.Document theDocument = theDocumentBuilder.parse(aFile);
        theDocument.getDocumentElement().normalize();

        return (theDocument);
    }

    //This module extracts the information from the directory.xml file using an
    //XML reader that creates a DOM from which the method creates a user-defined
    //map between view names and ViewModel objects. In the future, we might 
    //utilize a SAX XML reader skipping the creation of the DOM and directly 
    //create the ViewModel map data structure. This should result in a slight 
    //processing speed up although we will only be doing it once during a servlet
    //session and storing the utlimate view model map in the servlet's session
    //object. Directory.xml represents a model mapping for the entire application.
    //Based upon the length of this method ( 50+ lines of code ) it should 
    //likely be subdivided and invoke a few private utility methods for 
    //completing its work.

    java.util.HashMap<String, ViewModel> populatingTheDirectoryMap(javax.servlet.ServletContext theServletContext) throws Exception {

        String aViewId = null;
        String aTileId = null;
        String aTileClass = null;
        String aTileMethod = null;

        aDirectoryMap = new java.util.HashMap<String, ViewModel>();

        org.w3c.dom.Document theDocument = gettingTheDirectoryXMLDocument(theServletContext);

        org.w3c.dom.NodeList theModelNodeList = theDocument.getElementsByTagName("model");

        for (int aModelNodeIndex = 0; aModelNodeIndex < theModelNodeList.getLength(); aModelNodeIndex++) {

            org.w3c.dom.Node aModelNode = theModelNodeList.item(aModelNodeIndex);

            if (aModelNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {

                org.w3c.dom.Element aModelElement = (org.w3c.dom.Element) aModelNode;
                org.w3c.dom.NodeList theViewNodeList
                        = aModelElement.getElementsByTagName("view");

                for (int aViewNodeIndex = 0; aViewNodeIndex < theViewNodeList.getLength(); aViewNodeIndex++) {

                    org.w3c.dom.Node aViewNode = (org.w3c.dom.Node) theViewNodeList.item(aViewNodeIndex);

                    if (aViewNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {

                        org.w3c.dom.Element aViewElement = (org.w3c.dom.Element) aViewNode;

                        ViewModel aViewModel = new ViewModel();
                        aViewId = aViewElement.getAttribute("id");

                        aViewModel.settingTheStencilFileName(aViewElement.getAttribute("stencil"));
                        aViewModel.settingTheStencilContentType(aViewElement.getAttribute("content-type"));

                        java.util.HashMap<String, String> someTileRendererMappings = new java.util.HashMap<String, String>();
                        org.w3c.dom.NodeList theTileNodeList = aViewElement.getElementsByTagName("tile");

                        for (int aTileNodeIndex = 0; aTileNodeIndex < theTileNodeList.getLength(); aTileNodeIndex++) {

                            org.w3c.dom.Node aTileNode = (org.w3c.dom.Node) theTileNodeList.item(aTileNodeIndex);

                            if (aTileNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                                org.w3c.dom.Element aTileElement = (org.w3c.dom.Element) aTileNode;

                                aTileId = aTileElement.getAttribute("id");
                                aTileClass = aTileElement.getAttribute("class");
                                aTileMethod = aTileElement.getAttribute("method");

                                someTileRendererMappings.put(aTileId, aTileClass + "," + aTileMethod);

                            }

                        }
                        aViewModel.settingTheTileRendererMappings(someTileRendererMappings);
                        aDirectoryMap.put(aViewId, aViewModel);
                    }
                }
            }
        }
        //Closing Contract
        //The directory map cannot be empty if the CAB returns a view
        if (aDirectoryMap.size() == 0) {
            throw new Exception(DIRECTORY_ERROR);
        }
        return (aDirectoryMap);
    }
    
        /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    public void processRequest(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, javax.servlet.ServletContext theServletContext)
            throws javax.servlet.ServletException, java.io.IOException {
        response.setContentType("text/html;charset=UTF-8");
        java.io.PrintWriter out = response.getWriter();
        Engine aCAB = new Engine();        
        javax.servlet.http.HttpServletRequestWrapper aRequestWrapper = new javax.servlet.http.HttpServletRequestWrapper( request );
        javax.servlet.http.HttpServletResponseWrapper aResponseWrapper = new javax.servlet.http.HttpServletResponseWrapper( response );
        try {                      
            out.println(aCAB.preparingTheSuccessfulResponse(aRequestWrapper.getParameter("view"),theServletContext, aRequestWrapper, aResponseWrapper));            
        } catch (Exception e) {
            
            ((javax.servlet.http.HttpSession) aRequestWrapper.getSession()).setAttribute(EXCEPTION_KEY,e.getMessage());
           out.println(aCAB.preparingTheExceptionResponse(theServletContext, aRequestWrapper, aResponseWrapper));
             
            e.printStackTrace(out);
        }        
    }

}
