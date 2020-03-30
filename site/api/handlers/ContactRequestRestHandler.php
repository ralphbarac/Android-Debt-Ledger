<?php
    #require_once("SimpleRestHandler.php");

    class ContactRequestRestHandler extends SimpleRestHandler
    {
        public function pending($id)
        {
            $connection = new mysqli("localhost", "cs4474_client", "egbX2W0Ucz", "cs4474_project");
            $query = "SELECT id, first_name, last_name, email FROM user WHERE id IN (SELECT user from contact_request WHERE contact=".$id." AND status='pending')";

            if($result = $connection->query($query))
            {
                while($row = $result->fetch_assoc())
                {
                    $response[] = $row;
                }
                
                if($response === NULL)
                {
                    $response[] = array("result" => "no requests found"); 
                }
            }

            echo json_encode($response);
            $result->close();
            $connection->close();
        }
        
        public function add($user, $contact)
        {
            $connection = new mysqli("localhost", "cs4474_client", "egbX2W0Ucz", "cs4474_project");
            $query = "INSERT INTO contact_request (user, contact) VALUES (user=".$user.", contact=".$contact.")";

            if($result = $connection->query($query))
            {
                if($result->affected_rows > 0)
                {
                    $query = "SELECT * FROM contact_request WHERE user=".$user.", contact=".$contact;
                    
                    if($result = $connection->query($query))
                    {
                        while($row = $result->fetch_assoc())
                        {
                            $response[] = $row;
                        }
                    }
                    else
                    {
                        $response[] = array("result" => $connection->error);
                    }
                }
                else
                {
                    $response[] = array("result" => $connection->error);
                }
            }
            else
            {
                $response[] = array("result" => $connection->error);
            }

            echo json_encode($response);
            $result->close();
            $connection->close();
        }

        public function accept($user, $contact)
        {
            $connection = new mysqli("localhost", "cs4474_client", "egbX2W0Ucz", "cs4474_project");
            $query = "UPDATE contact_request SET status='accepted' WHERE user=".$user." AND contact=".$contact;
            
            if($result = $connection->query($query))
            {
                if($result->affected_rows > 0)
                {
                    $query = "INSERT INTO contact (user, contact) (VALUES user=".$user.", contact=".$contact.")";
                    $query2 = "INSERT INTO contact (user, contact) (VALUES user=".$contact.", contact=".$user.")";
                
                    if(($result = $connection->query($query)) && ($result2 = $connection->query($query2)))
                    {
                        $query = "SELECT * FROM contact_request WHERE user=".$user.", contact=".$contact;

                        if($result = $connection->query($query))
                        {
                            while($row = $result->fetch_assoc())
                            {
                                $response[] = $row;
                            }
                        }
                        else
                        {
                            $response[] = array("result" => $connection->error);
                        }
                    }
                    else
                    {
                        $response[] = array("result" => $connection->error);
                    }
                }
                else
                {
                    $response[] = array("result" => "request not found");
                }
            }
            else
            {
                $response[] = array("result" => $connection->error);
            }

            echo json_encode($response);
            $result->close();
            $connection->close();
        }

        public function deny($user, $contact)
        {
            $connection = new mysqli("localhost", "cs4474_client", "egbX2W0Ucz", "cs4474_project");
            $query = "UPDATE contact_request SET status='denied' WHERE user=".$user." AND contact=".$contact;

            if($result = $connection->query($query))
            {
                if($result->affected_rows > 0)
                {
                    $response[] = $row;
                }
                else
                {
                    $response[] = array("result" => "request not found");
                }
            }
            else
            {
                $response[] = array("result" => $connection->error);
            }

            echo json_encode($response);
            $result->close();
            $connection->close();
        }

        /*
        public function reply($rawdata)
        {
            $requestContentType = $_SERVER['HTTP_ACCEPT'];
            $this->setHttpHeaders($requestContentType, $statusCode);
                    
            if(strpos($requestContentType,'application/json') !== false)
            {
                $response = $this->encodeJson($rawData);
                echo $response;
            }
            else if(strpos($requestContentType,'text/html') !== false)
            {
                $response = $this->encodeHtml($rawData);
                echo $response;
            }
            else if(strpos($requestContentType,'application/xml') !== false)
            {
                $response = $this->encodeXml($rawData);
                echo $response;
            }
        }
        
        public function encodeHtml($responseData)
        {
            $htmlResponse = "<table border='1'>";
            foreach($responseData as $key=>$value)
            {
                $htmlResponse .= "<tr><td>". $key. "</td><td>". $value. "</td></tr>";
            }
            $htmlResponse .= "</table>";
            return $htmlResponse;		
        }
        
        public function encodeJson($responseData)
        {
            $jsonResponse = json_encode($responseData);
            return $jsonResponse;		
        }
        
        public function encodeXml($responseData)
        {
            // creating object of SimpleXMLElement
            $xml = new SimpleXMLElement('<?xml version="1.0"?><mobile></mobile>');
            foreach($responseData as $key=>$value)
            {
                $xml->addChild($key, $value);
            }
            return $xml->asXML();
        }
        */
    }
?>