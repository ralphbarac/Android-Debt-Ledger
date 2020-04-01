<?php
    #require_once("SimpleRestHandler.php");

    class TransactionRestHandler
    {
        public function id($id)
        {
            $connection = new mysqli("cs4474-debt-ledger.chv9hyuyepg2.us-east-2.rds.amazonaws.com", "admin", "I6leZnstPdI7SSqameT4", "debt_ledger");
            $query = "SELECT * FROM transaction WHERE id=".$id;

            if($result = $connection->query($query))
            {
                while($row = $result->fetch_assoc())
                {
                    $response[] = $row;
                }
                
                if($response === NULL)
                {
                    $response[] = array("result" => "transaction not found"); 
                }
            }

            $result->close();
            $connection->close();
            $this->reply($rawdata);
        }

        public function debtor($id)
        {
            $connection = new mysqli("cs4474-debt-ledger.chv9hyuyepg2.us-east-2.rds.amazonaws.com", "admin", "I6leZnstPdI7SSqameT4", "debt_ledger");
            $query = "SELECT * FROM transaction WHERE debtor=".$id;

            if($result = $connection->query($query))
            {
                while($row = $result->fetch_assoc())
                {
                    $response[] = $row;
                }
                
                if($response === NULL)
                {
                    $response[] = array("result" => "no transactions found"); 
                }
            }

            $result->close();
            $connection->close();
            $this->reply($rawdata);
        }

        public function creditor($id)
        {
            $connection = new mysqli("cs4474-debt-ledger.chv9hyuyepg2.us-east-2.rds.amazonaws.com", "admin", "I6leZnstPdI7SSqameT4", "debt_ledger");
            $query = "SELECT * FROM transaction WHERE creditor=".$id;

            if($result = $connection->query($query))
            {
                while($row = $result->fetch_assoc())
                {
                    $response[] = $row;
                }
                
                if($response === NULL)
                {
                    $response[] = array("result" => "no transactions found"); 
                }
            }

            $result->close();
            $connection->close();
            $this->reply($rawdata);
        }

        public function add($input)
        {
            $connection = new mysqli("cs4474-debt-ledger.chv9hyuyepg2.us-east-2.rds.amazonaws.com", "admin", "I6leZnstPdI7SSqameT4", "debt_ledger");
            $info = json_decode($input);
            
            if(isset($info->description))
            {
                $query = "INSERT INTO transaction (debtor, creditor, description, amount) VALUES (".$info->debtor.", ".$info->creditor.", '".$info->description."', ".$info->amount.")";
            }
            else
            {
                $query = "INSERT INTO transaction (debtor, creditor, amount) VALUES (".$info->debtor.", ".$info->creditor.", ".$info->amount.")";
            }
            
            if($result = $connection->query($query))
            {
                if($connection->affected_rows > 0)
                {
                    $query = "SELECT * FROM transaction WHERE id=".$result->insert_id;
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

            echo json_encode($response);
            $result->close();
            $connection->close();
        }

        public function computeBalances($id) {
            $connection = new mysqli("cs4474-debt-ledger.chv9hyuyepg2.us-east-2.rds.amazonaws.com", "admin", "I6leZnstPdI7SSqameT4", "debt_ledger");
            $query = "SELECT id, first_name, last_name, email, SUM(balances.balance) AS balance FROM
                        (SELECT contact.contact, debts.balance FROM contact LEFT JOIN (SELECT creditor AS contact, SUM(amount*-1) AS balance FROM transaction WHERE debtor = ".$id." GROUP BY creditor) AS debts ON contact.contact = debts.contact WHERE user = ".$id."
                        UNION
                        SELECT contact.contact, credits.balance FROM contact LEFT JOIN (SELECT debtor AS contact, SUM(amount) AS balance FROM transaction WHERE creditor = ".$id." GROUP BY debtor) AS credits ON contact.contact = credits.contact WHERE user = ".$id.") AS balances INNER JOIN user ON balances.contact = user.id
                        GROUP BY balances.contact;";

            if($result = $connection->query($query))
            {
                while($row = $result->fetch_assoc())
                {
                    $response[] = $row;
                }
                $result->close();

                if($response === NULL)
                {
                    $response[] = array("empty" => "no contacts found"); 
                }
            }
            else
            {
                $response[] = array("result" => $connection->error);
            }

            echo json_encode($response);
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